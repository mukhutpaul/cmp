package com.cm_policier.effectifs.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "policier")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1
    @Column(name = "matricule", unique = true)
    private String matricule;

    // 2
    @Column(name = "lastname")
    private String lastname;

    // 3
    @Column(name = "postname")
    private String postname;

    // 4
    @Column(name = "firstnames")
    private String firstnames;

    // 5
    @Column(name = "birth_date")
    private LocalDate birthDate;

    // 6
    @Column(name = "gender")
    private String gender;

    // 7
    @Column(name = "city_birth")
    private String cityBirth;

    // 8
    @Column(name = "lieu")
    private String lieu;

    // 9
    @Column(name = "country_birth")
    private String countryBirth;

    // 10
    @Column(name = "date_added")
    private LocalDate dateAdded;

    // 11
    @Column(name = "rank")
    private String rank;

    // 12
    @Column(name = "rank_nomination_act_date")
    private LocalDate rankNominationActDate;

    // 13
    @Column(name = "date_entry_in_police")
    private LocalDate dateEntryInPolice;

    // 14
    @Column(name = "profession")
    private String profession;

    // 15
    @Column(name = "profession_start_date")
    private LocalDate professionStartDate;

    // 16
    @Column(name = "main_unit")
    private String mainUnit;

    // 17
    @Column(name = "unit")
    private String unit;

    // 18
    @Column(name = "spouse_lastname")
    private String spouseLastname;

    // 19
    @Column(name = "spouse_postname")
    private String spousePostname;

    // 20
    @Column(name = "spouse_firstname")
    private String spouseFirstname;

    // 21
    @Column(name = "spouse_nationality")
    private String spouseNationality;

    // 22
    @Column(name = "spouse_profession")
    private String spouseProfession;

    // 23
    @Column(name = "bloodtype")
    private String bloodtype;

    // 24
    @Column(name = "district_origin")
    private String districtOrigin;

    // 25
    @Column(name = "territoire_origin")
    private String territoireOrigin;

    // 26
    @Column(name = "village_origin")
    private String villageOrigin;

    // 27
    @Column(name = "address_street")
    private String addressStreet;

    // 28
    @Column(name = "address_commune")
    private String addressCommune;

    // 29
    @Column(name = "telephone")
    private String telephone;

    // 30
    @Column(name = "emergency_lastname")
    private String emergencyLastname;

    // 31
    @Column(name = "emergency_postname")
    private String emergencyPostname;

    // 32
    @Column(name = "emergency_firstname")
    private String emergencyFirstname;

    // 33
    @Column(name = "emergency_relation")
    private String emergencyRelation;

    // 34
    @Column(name = "emergency_address_street")
    private String emergencyAddressStreet;

    // 35
    @Column(name = "emergency_address_commune")
    private String emergencyAddressCommune;

    // 36
    @Column(name = "emergency_telephone")
    private String emergencyTelephone;

    // 37
    @Column(name = "position")
    private String position;

    // 38
    @Column(name = "pk_photo", unique = true)
    private String pkPhoto;
}