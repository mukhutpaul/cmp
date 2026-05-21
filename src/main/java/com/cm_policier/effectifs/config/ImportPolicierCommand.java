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

        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(fis);

        Sheet sheet = workbook.getSheetAt(0);

        // Ignore la ligne d'entête
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            if (row == null) {
                continue;
            }

            Policier policier = Policier.builder()

                    .matricule(getString(row.getCell(0)))
                    .lastname(getString(row.getCell(1)))
                    .postname(getString(row.getCell(2)))
                    .firstnames(getString(row.getCell(3)))
                    .birthDate(getDate(row.getCell(4)))
                    .gender(getString(row.getCell(5)))
                    .cityBirth(getString(row.getCell(6)))
                    .lieu(getString(row.getCell(7)))
                    .dateAdded(getDate(row.getCell(8)))
                    .rank(getString(row.getCell(9)))
                    .rankNominationActDate(getDate(row.getCell(10)))
                    .dateEntryInPolice(getDate(row.getCell(11)))
                    .profession(getString(row.getCell(12)))
                    .professionStartDate(getDate(row.getCell(13)))
                    .mainUnit(getString(row.getCell(14)))
                    .unit(getString(row.getCell(15)))
                    .spouseLastname(getString(row.getCell(16)))
                    .spousePostname(getString(row.getCell(17)))
                    .spouseFirstname(getString(row.getCell(18)))
                    .spouseNationality(getString(row.getCell(19)))
                    .spouseProfession(getString(row.getCell(20)))
                    .bloodtype(getString(row.getCell(21)))
                    .districtOrigin(getString(row.getCell(22)))
                    .territoireOrigin(getString(row.getCell(23)))
                    .villageOrigin(getString(row.getCell(24)))
                    .addressStreet(getString(row.getCell(25)))
                    .addressCommune(getString(row.getCell(26)))
                    .telephone(getString(row.getCell(27)))
                    .emergencyLastname(getString(row.getCell(28)))
                    .emergencyPostname(getString(row.getCell(29)))
                    .emergencyFirstname(getString(row.getCell(30)))
                    .emergencyRelation(getString(row.getCell(31)))
                    .emergencyAddressStreet(getString(row.getCell(32)))
                    .emergencyAddressCommune(getString(row.getCell(33)))
                    .emergencyTelephone(getString(row.getCell(34)))
                    .position(getString(row.getCell(35)))
                    .pkPhoto(getString(row.getCell(36)))

                    .build();

            policierRepository.save(policier);
        }

        workbook.close();
        fis.close();

        System.out.println("Importation terminée !");
    }

    private String getString(Cell cell) {

        if (cell == null) {
            return null;
        }

        cell.setCellType(CellType.STRING);

        return cell.getStringCellValue().trim();
    }

    private LocalDate getDate(Cell cell) {

        if (cell == null) {
            return null;
        }

        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        return null;
    }
}
