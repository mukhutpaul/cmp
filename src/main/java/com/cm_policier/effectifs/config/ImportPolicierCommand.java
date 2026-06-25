package com.cm_policier.effectifs.config;

import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.PolicierRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Order(1)
@RequiredArgsConstructor
public class ImportPolicierCommand implements CommandLineRunner {

    private final PolicierRepository policierRepository;

    private static final DataFormatter FORMATTER = new DataFormatter();
    private static final int BATCH_SIZE = 1000;

    @Override
    public void run(String... args) throws Exception {

        IOUtils.setByteArrayMaxOverride(500_000_000);

        String filePath = "/bdd/db_controle.xlsx";
        //String filePath = "c:/bdd/db_controle.xlsx";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("❌ Fichier introuvable : " + filePath);
            return;
        }

        System.out.println("📄 Fichier trouvé : " + filePath);
        System.out.println("📦 Taille : " + (file.length() / (1024 * 1024)) + " MB");

        int imported = 0;
        int errors = 0;
        int duplicates = 0;
        int totalRows = 0; // ✅ IMPORTANT : déclaré ici (global méthode)

        List<Policier> batch = new ArrayList<>(BATCH_SIZE);

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(fis)
        ) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                System.out.println("❌ Aucune feuille trouvée.");
                return;
            }

            Map<String, Integer> headerMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                System.out.println("❌ Ligne d'entête introuvable.");
                return;
            }

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) continue;

                String header = FORMATTER.formatCellValue(cell)
                        .trim()
                        .toUpperCase();

                headerMap.put(header, i);
            }

            totalRows = sheet.getLastRowNum(); // ✅ assignation ici

            if (totalRows <= 0) {
                System.out.println("❌ Aucune donnée à importer.");
                return;
            }

            System.out.println("📊 Nombre de lignes : " + totalRows);
            System.out.println("🚀 Début import...");

            for (int i = 1; i <= totalRows; i++) {

                // 📈 progression
                if (i % 1000 == 0 || i == totalRows) {

                    double percent = (i * 100.0) / totalRows;

                    System.out.printf(
                            "📈 %.2f%% | Ligne %d/%d | Importés=%d | Doublons=%d | Erreurs=%d%n",
                            percent,
                            i,
                            totalRows,
                            imported,
                            duplicates,
                            errors
                    );
                }

                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {

                    String matricule = getString(row, headerMap, "matricule");
                    String pkPhoto = getString(row, headerMap, "policierId");

                    if (matricule == null || matricule.isBlank()) continue;

                    if (policierRepository.existsByMatricule(matricule)) {
                        duplicates++;
                        continue;
                    }

                    if (pkPhoto != null && policierRepository.existsByPkPhoto(pkPhoto)) {
                        duplicates++;
                        continue;
                    }

                    Policier policier = Policier.builder()
                            .matricule(matricule)
                            .lastname(getString(row, headerMap, "nom"))
                            .postname(getString(row, headerMap, "postnom"))
                            .firstnames(getString(row, headerMap, "prenom"))
                            .birthDate(getDate(row, headerMap, "dateNaissance"))
                            .gender(getString(row, headerMap, "genre"))
                            .cityBirth(getString(row, headerMap, "lieuNaissance"))
                            .dateAdded(getDate(row, headerMap, "dateAdded"))
                            .rank(getString(row, headerMap, "grade"))
                            .dateEntryInPolice(getDate(row, headerMap, "dateIncorporation"))
                            .unit(getString(row, headerMap, "unite"))
                            .bloodtype(getString(row, headerMap, "groupeSanguin"))
                            .position(getString(row, headerMap, "position"))
                            .pkPhoto(pkPhoto)
                            .build();

                    batch.add(policier);

                    if (batch.size() >= BATCH_SIZE) {
                        policierRepository.saveAll(batch);
                        imported += batch.size();
                        batch.clear();
                    }

                } catch (Exception e) {
                    errors++;
                    System.out.println("❌ Erreur ligne " + i + " : " + e.getMessage());
                }
            }

            if (!batch.isEmpty()) {
                policierRepository.saveAll(batch);
                imported += batch.size();
                batch.clear();
            }

        }

        System.out.println("\n====================================");
        System.out.println("🎉 IMPORT TERMINÉ");
        System.out.println("====================================");
        System.out.println("📊 Total lignes : " + totalRows);
        System.out.println("✔ Importés      : " + imported);
        System.out.println("🔁 Doublons      : " + duplicates);
        System.out.println("❌ Erreurs       : " + errors);
        System.out.println("====================================");
    }

    private String getString(Row row, Map<String, Integer> map, String column) {

        Integer index = map.get(column.toUpperCase());
        if (index == null) return null;

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        String value = FORMATTER.formatCellValue(cell);
        if (value == null) return null;

        value = value.trim();

        if (value.isEmpty()) return null;

        if (value.equalsIgnoreCase("null")
                || value.equalsIgnoreCase("Sans fonction")
                || value.equalsIgnoreCase("Sans grade")) {
            return null;
        }

        return value;
    }

    private LocalDate getDate(Row row, Map<String, Integer> map, String column) {

        Integer index = map.get(column.toUpperCase());
        if (index == null) return null;

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        try {

            if (cell.getCellType() == CellType.NUMERIC
                    && DateUtil.isCellDateFormatted(cell)) {

                return cell.getDateCellValue()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            String value = getString(row, map, column);
            if (value == null) return null;

            DateTimeFormatter[] formats = {
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                    DateTimeFormatter.ofPattern("d/M/yyyy"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            };

            for (DateTimeFormatter f : formats) {
                try {
                    return LocalDate.parse(value, f);
                } catch (Exception ignored) {}
            }

        } catch (Exception e) {
            System.out.println("⚠ Date invalide colonne " + column + " ligne " + row.getRowNum());
        }

        return null;
    }
}