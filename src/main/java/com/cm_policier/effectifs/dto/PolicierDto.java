package com.cm_policier.effectifs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicierDto {

    private Long id;

    // 1
    private String matricule;

    // 2
    private String lastname;

    // 3
    private String postname;

    // 4
    private String firstnames;

    // 5
    private LocalDate birthDate;

    // 6
    private String gender;

    // 7
    private String cityBirth;

    // 8
    private String lieu;

    // 9
    private String countryBirth;

    // 10
    private LocalDate dateAdded;

    // 11
    private String rank;

    // 12
    private LocalDate rankNominationActDate;

    // 13
    private LocalDate dateEntryInPolice;

    // 14
    private String profession;

    // 15
    private LocalDate professionStartDate;

    // 16
    private String mainUnit;

    // 17
    private String unit;

    // 18
    private String spouseLastname;

    // 19
    private String spousePostname;

    // 20
    private String spouseFirstname;

    // 21
    private String spouseNationality;

    // 22
    private String spouseProfession;

    // 23
    private String bloodtype;

    // 24
    private String districtOrigin;

    // 25
    private String territoireOrigin;

    // 26
    private String villageOrigin;

    // 27
    private String addressStreet;

    // 28
    private String addressCommune;

    // 29
    private String telephone;

    // 30
    private String emergencyLastname;

    // 31
    private String emergencyPostname;

    // 32
    private String emergencyFirstname;

    // 33
    private String emergencyRelation;

    // 34
    private String emergencyAddressStreet;

    // 35
    private String emergencyAddressCommune;

    // 36
    private String emergencyTelephone;

    // 37
    private String position;

    // 38
    private String pkPhoto;

    // URL IMAGE
    private String photoUrl;
}