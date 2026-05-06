package com.cm_policier.effectifs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Policier {
    @Id @GeneratedValue
    private Long id;

    private String matricule;
    private String nom;
    private String grade;
    private String unite;
    private boolean actif;
}