package com.cm_policier.effectifs.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Controle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String uid;

    @ManyToOne
    private Policier policier;
    

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