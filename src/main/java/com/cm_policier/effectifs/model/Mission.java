package com.cm_policier.effectifs.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mission {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @ManyToOne
    private User chargeMission;

    private String zone;
    private String numero;

    private Boolean isActive;
}