package com.cm_policier.effectifs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "policier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String matricule;

    private String nom;
    private String postnom;
    private String prenom;

    @Column(length = 1)
    private String sexe;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String villeNaissance;
    private String villageNaissance;
    private String paysDeNaissance;

    private Integer taille;
    private String couleurYeux;
    private String telephone;
    private String email;
    private String unite;

    @Column(columnDefinition = "TEXT")
    private String adresse;


    private String commune;
    private LocalDate dateEntreePolice;
    private String statut;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    private String groupeSanguin;
    private String provinceOrigin;
    private String profession;
    private LocalDate professionStart;

    private String villeIntegration;
    private String enrolmentsCommissariat;
    private String enrolmentsProvince;

    private LocalDate dateMarriage;
    private String etatcivil;

    private String districtOrigin;
    private String territoireOrigin;
    private String secteurOrigin;
    private String villageOrigin;

    private String originAdminMinistry;
    private String originAdminService;
    private String originAdminGrade;

    private String origAdminNominatifAct;
    private LocalDate origAdminNominatifActDate;
    private LocalDate origAdminEntryDate;
    private String orgnAdminEntryPlace;

    private String distinction;
    private LocalDate distinctionDate;
    private String distinctionNr;
    private String distinctionMention;

    private String adresseInstitutionNr;
    private String adresseInstitutionAvenue;
    private String adresseInstitutionCommune;

    private String persRefNom;
    private String persRefPostnom;
    private String persRefPrenom;
    private String persRefFiliation;
    private String persRefAdresseNr;
    private String persRefAdresseAvenue;
    private String persRefAdresseCommune;
    private String persRefAdresseTelephone;

    private String policierNumPermis;
    private String policierCategoriePermis;
    private LocalDate policierDatePermis;

    private String observation;
    private String sport;

    // ================= LANGUES (SAFE DEFAULT = false) =================

    @Builder.Default
    @Column(nullable = false)
    private Boolean francaisParle = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean francaisEcrit = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean lingalaParle = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean lingalaEcrit = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean kikongoParle = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean kikongoEcrit = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean swahiliParle = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean swahiliEcrit = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean tshilubaParle = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean tshilubaEcrit = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean englishParle = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean englishEcrit = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean autreLangueParle = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean autreLangueEcrit = true;

    private String autresLangues;

    private Integer pcount;

    @Column(length = 15)
    private String position;
}