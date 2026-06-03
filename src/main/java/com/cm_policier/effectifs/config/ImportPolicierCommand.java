package com.cm_policier.effectifs.config;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.PolicierRepository;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(1)
@RequiredArgsConstructor
public class ImportPolicierCommand implements CommandLineRunner {

    private final PolicierRepository policierRepository;

    @Override
    public void run(String... args) throws Exception {

        String filePath = "C:/bdd/db_controle.xlsx";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("❌ Fichier introuvable : " + filePath);
            return;
        }

        int imported = 0;
        int errors = 0;
        int duplicates = 0;

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // ================================
            // 🔥 HEADER MAP (IMPORTANT FIX)
            // ================================
            Map<String, Integer> headerMap = new HashMap<>();
            Row headerRow = sheet.getRow(0);

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) continue;

                String header = cell.getStringCellValue().trim().toUpperCase();
                headerMap.put(header, i);
            }

            // ================================
            // 🔁 DATA LOOP
            // ================================
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {

                    String matricule = getString(row, headerMap, "MATRICULE");
                    String pkPhoto = getString(row, headerMap, "PK_PHOTO");

                    if (matricule == null || matricule.isBlank()) continue;

                    // 🔥 doublon matricule
                    if (policierRepository.existsByMatricule(matricule)) {
                        System.out.println("🔁 MATRICULE DUP: " + matricule + " ligne " + i);
                        duplicates++;
                        continue;
                    }

                    // 🔥 doublon pk_photo
                    if (pkPhoto != null && policierRepository.existsByPkPhoto(pkPhoto)) {
                        System.out.println("🔁 PK_PHOTO DUPLICATE DETECTED");
                        System.out.println("   ligne : " + i);
                        System.out.println("   pkPhoto : " + pkPhoto);
                        System.out.println("   matricule : " + matricule);
                        System.out.println("   nom : " + getString(row, headerMap, "LASTNAME"));
                        duplicates++;
                        continue;
                    }

                    Policier policier = Policier.builder()
                            .matricule(matricule)
                            .lastname(getString(row, headerMap, "LASTNAME"))
                            .postname(getString(row, headerMap, "POSTNAME"))
                            .firstnames(getString(row, headerMap, "FIRSTNAMES"))
                            .birthDate(getDate(row, headerMap, "BIRTH_DATE"))
                            .gender(getString(row, headerMap, "GENDER"))
                            .cityBirth(getString(row, headerMap, "CITY_BIRTH"))
                            .lieu(getString(row, headerMap, "LIEU"))
                            .countryBirth(getString(row, headerMap, "COUNTRY_BIRTH"))
                            .dateAdded(getDate(row, headerMap, "DATE_ADDED"))
                            .rank(getString(row, headerMap, "RANK"))
                            .rankNominationActDate(getDate(row, headerMap, "RANK_NOMINATION_ACT_DATE"))
                            .dateEntryInPolice(getDate(row, headerMap, "DATE_ENTRY_IN_POLICE"))
                            .profession(getString(row, headerMap, "PROFESSION"))
                            .professionStartDate(getDate(row, headerMap, "PROFESSION_START_DATE"))
                            .mainUnit(getString(row, headerMap, "MAIN_UNIT"))
                            .unit(getString(row, headerMap, "UNIT"))
                            .spouseLastname(getString(row, headerMap, "SPOUSE_LASTNAME"))
                            .spousePostname(getString(row, headerMap, "SPOUSE_POSTNAME"))
                            .spouseFirstname(getString(row, headerMap, "SPOUSE_FIRSTNAME"))
                            .spouseNationality(getString(row, headerMap, "SPOUSE_NATIONALITY"))
                            .spouseProfession(getString(row, headerMap, "SPOUSE_PROFESSION"))
                            .bloodtype(getString(row, headerMap, "BLOODTYPE"))
                            .districtOrigin(getString(row, headerMap, "DISTRICT_ORIGIN"))
                            .territoireOrigin(getString(row, headerMap, "TERRITOIRE_ORIGIN"))
                            .villageOrigin(getString(row, headerMap, "VILLAGE_ORIGIN"))
                            .addressStreet(getString(row, headerMap, "ADDRESS_STREET"))
                            .addressCommune(getString(row, headerMap, "ADDRESS_COMMUNE"))
                            .telephone(getString(row, headerMap, "TELEPHONE"))
                            .emergencyLastname(getString(row, headerMap, "EMERGENCY_LASTNAME"))
                            .emergencyPostname(getString(row, headerMap, "EMERGENCY_POSTNAME"))
                            .emergencyFirstname(getString(row, headerMap, "EMERGENCY_FIRSTNAME"))
                            .emergencyRelation(getString(row, headerMap, "EMERGENCY_RELATION"))
                            .emergencyAddressStreet(getString(row, headerMap, "EMERGENCY_ADDRESS_STREET"))
                            .emergencyAddressCommune(getString(row, headerMap, "EMERGENCY_ADDRESS_COMMUNE"))
                            .emergencyTelephone(getString(row, headerMap, "EMERGENCY_TELEPHONE"))
                            .position(getString(row, headerMap, "POSITION"))
                            .pkPhoto(pkPhoto)
                            .build();

                    policierRepository.save(policier);
                    imported++;

                } catch (Exception e) {
                    errors++;
                    System.out.println("❌ Erreur ligne " + i + " : " + e.getMessage());
                }
            }
        }

        System.out.println("\n===== IMPORT TERMINÉ =====");
        System.out.println("✔ Importés : " + imported);
        System.out.println("🔁 Doublons : " + duplicates);
        System.out.println("❌ Erreurs : " + errors);
    }

    // ================================
    // 🔥 SAFE STRING READER
    // ================================
    private String getString(Row row, Map<String, Integer> map, String column) {

        Integer index = map.get(column.toUpperCase());
        if (index == null) return null;

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);

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

    // ================================
    // 🔥 SAFE DATE READER
    // ================================
    private LocalDate getDate(Row row, Map<String, Integer> map, String column) {

        Integer index = map.get(column.toUpperCase());
        if (index == null) return null;

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        try {

            if (cell.getCellType() == CellType.NUMERIC &&
                    DateUtil.isCellDateFormatted(cell)) {

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