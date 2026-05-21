package com.cm_policier.effectifs.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "policier",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_policier_matricule", columnNames = "matricule"),
        @UniqueConstraint(name = "uk_policier_pk_photo", columnNames = "pk_photo")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "matricule", nullable = false, unique = true, length = 255)
    private String matricule;

    @Column(name = "lastname", length = 255)
    private String lastname;

    @Column(name = "postname", length = 255)
    private String postname;

    @Column(name = "firstnames", length = 255)
    private String firstnames;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", length = 255)
    private String gender;

    @Column(name = "city_birth", length = 255)
    private String cityBirth;

    @Column(name = "lieu", length = 255)
    private String lieu;

    @Column(name = "date_added")
    private LocalDate dateAdded;

    @Column(name = "rank", length = 255)
    private String rank;

    @Column(name = "rank_nomination_act_date")
    private LocalDate rankNominationActDate;

    @Column(name = "date_entry_in_police")
    private LocalDate dateEntryInPolice;

    @Column(name = "profession", length = 255)
    private String profession;

    @Column(name = "profession_start_date")
    private LocalDate professionStartDate;

    @Column(name = "main_unit", length = 255)
    private String mainUnit;

    @Column(name = "unit", length = 255)
    private String unit;

    @Column(name = "spouse_lastname", length = 255)
    private String spouseLastname;

    @Column(name = "spouse_postname", length = 255)
    private String spousePostname;

    @Column(name = "spouse_firstname", length = 255)
    private String spouseFirstname;

    @Column(name = "spouse_nationality", length = 255)
    private String spouseNationality;

    @Column(name = "spouse_profession", length = 255)
    private String spouseProfession;

    @Column(name = "bloodtype", length = 255)
    private String bloodtype;

    @Column(name = "district_origin", length = 255)
    private String districtOrigin;

    @Column(name = "territoire_origin", length = 255)
    private String territoireOrigin;

    @Column(name = "village_origin", length = 255)
    private String villageOrigin;

    @Column(name = "address_street", length = 255)
    private String addressStreet;

    @Column(name = "address_commune", length = 255)
    private String addressCommune;

    @Column(name = "telephone", length = 255)
    private String telephone;

    @Column(name = "emergency_lastname", length = 255)
    private String emergencyLastname;

    @Column(name = "emergency_postname", length = 255)
    private String emergencyPostname;

    @Column(name = "emergency_firstname", length = 255)
    private String emergencyFirstname;

    @Column(name = "emergency_relation", length = 255)
    private String emergencyRelation;

    @Column(name = "emergency_address_street", length = 255)
    private String emergencyAddressStreet;

    @Column(name = "emergency_address_commune", length = 255)
    private String emergencyAddressCommune;

    @Column(name = "emergency_telephone", length = 255)
    private String emergencyTelephone;

    @Column(name = "position", length = 255)
    private String position;

    @Column(name = "pk_photo", nullable = false, unique = true, length = 255)
    private String pkPhoto;
}