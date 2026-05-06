package com.cm_policier.effectifs.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Controle {

    @Id
    @GeneratedValue
    private Long id;

    private String uid;

    @ManyToOne
    private Person person;

    private Boolean present;
    private Boolean justifie;

    private String situation;
    private String status;

    private String matricule;
    private String unite;
    private String grade;

    private Boolean isActif;

    private LocalDateTime createdAt;
}