package com.cm_policier.effectifs.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PersonRequest {

    private String uuid;

    private String name;
    private String firstname;
    private String postname;

    private String sex;
    private String grade;

    private String unit;
    private String battalion;

    private LocalDate birthdate;
    private LocalDate nominationdate;

    private String province;
    private String district;

    private String status;

    private Integer idPersonnel;

    private String remplaceMilUuid;
}
