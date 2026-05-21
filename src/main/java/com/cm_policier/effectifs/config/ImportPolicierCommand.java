package com.cm_policier.effectifs.config;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.PolicierRepository;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ImportPolicierCommand implements CommandLineRunner {

    private final PolicierRepository policierRepository;

    @Override
    public void run(String... args) throws Exception {

        String filePath = "C:/bdd/db_controle.xlsx";

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Fichier Excel introuvable : " + filePath);
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {

                    Policier policier = Policier.builder()
                            .matricule(getString(row, 0))
                            .lastname(getString(row, 1))
                            .postname(getString(row, 2))
                            .firstnames(getString(row, 3))
                            .birthDate(getDate(row, 4))
                            .gender(getString(row, 5))
                            .cityBirth(getString(row, 6))
                            .lieu(getString(row, 7))
                            .dateAdded(getDate(row, 8))
                            .rank(getString(row, 9))
                            .rankNominationActDate(getDate(row, 10))
                            .dateEntryInPolice(getDate(row, 11))
                            .profession(getString(row, 12))
                            .professionStartDate(getDate(row, 13))
                            .mainUnit(getString(row, 14))
                            .unit(getString(row, 15))
                            .spouseLastname(getString(row, 16))
                            .spousePostname(getString(row, 17))
                            .spouseFirstname(getString(row, 18))
                            .spouseNationality(getString(row, 19))
                            .spouseProfession(getString(row, 20))
                            .bloodtype(getString(row, 21))
                            .districtOrigin(getString(row, 22))
                            .territoireOrigin(getString(row, 23))
                            .villageOrigin(getString(row, 24))
                            .addressStreet(getString(row, 25))
                            .addressCommune(getString(row, 26))
                            .telephone(getString(row, 27))
                            .emergencyLastname(getString(row, 28))
                            .emergencyPostname(getString(row, 29))
                            .emergencyFirstname(getString(row, 30))
                            .emergencyRelation(getString(row, 31))
                            .emergencyAddressStreet(getString(row, 32))
                            .emergencyAddressCommune(getString(row, 33))
                            .emergencyTelephone(getString(row, 34))
                            .position(getString(row, 35))
                            .pkPhoto(getString(row, 36))
                            .build();

                    if (policier.getMatricule() != null &&
                            !policierRepository.existsByMatricule(policier.getMatricule())) {

                        policierRepository.save(policier);
                        System.out.println("Importé : " + policier.getMatricule());
                    }

                } catch (Exception e) {
                    System.out.println("Erreur ligne " + i + " : " + e.getMessage());
                }
            }
        }

        System.out.println("Importation terminée !");
    }

    // 🔥 FIX IMPORTANT : lecture propre cellule
    private String getString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);

        if (value == null) return null;

        value = value.trim();

        if (value.isEmpty()) return null;

        // filtre valeurs parasites
        if (value.equalsIgnoreCase("Sans fonction")
                || value.equalsIgnoreCase("Sans grade")
                || value.equalsIgnoreCase("null")) {
            return null;
        }

        return value;
    }

    // 🔥 FIX DATE ROBUSTE
    private LocalDate getDate(Row row, int index) {

        Cell cell = row.getCell(index);
        if (cell == null) return null;

        try {

            // cas Excel date réelle
            if (cell.getCellType() == CellType.NUMERIC &&
                    DateUtil.isCellDateFormatted(cell)) {

                return cell.getDateCellValue()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            // cas texte
            String value = getString(row, index);

            if (value == null) return null;

            // ignore valeurs non-date
            if (!value.matches(".*\\d{4}.*")) {
                return null;
            }

            DateTimeFormatter[] formats = new DateTimeFormatter[]{
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
            System.out.println("Date invalide : " + cell);
        }

        return null;
    }
}