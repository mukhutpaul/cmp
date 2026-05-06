package com.cm_policier.effectifs.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetrieveList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String postnom;
    private String sexe;
    private String unite;
    private String matricule;
    private String grade;

    private Boolean isCmd = false;
}