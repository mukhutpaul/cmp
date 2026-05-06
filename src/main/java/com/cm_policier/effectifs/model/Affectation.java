package com.cm_policier.effectifs.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Affectation {
    @Id @GeneratedValue
    private Long id;

    private String lieu;
    private String poste;
    private LocalDate dateDebut;
}
