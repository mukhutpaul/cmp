package com.cm_policier.effectifs.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @Column(length = 40)
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

    @Column(name = "IdPersonnel")
    private Integer idPersonnel;

    // relation self (remplace_mil)
    @ManyToOne
    @JoinColumn(name = "remplace_Mil")
    private Person remplaceMil;
}