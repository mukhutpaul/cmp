package com.cm_policier.effectifs.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Est_Affecte")
public class EstAffecte {

    @Id
    @Column(name = "Id_Est_Affecte")
    private Integer id;

    @Column(name = "Date_Debut_Affectation")
    private LocalDateTime dateDebut;

    @Column(name = "Date_Fin_Affectation")
    private LocalDateTime dateFin;

    @Column(name = "Id_Unit_Affectation")
    private Integer unitId;

    @Column(name = "Id_Mil_Affecte")
    private String militaireId;

    private Boolean affecte;
}