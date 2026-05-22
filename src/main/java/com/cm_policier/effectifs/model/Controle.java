package com.cm_policier.effectifs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Controle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true)
    private String uid;

    // ===================== RELATIONS =====================

    @ManyToOne
    private Policier policier;

    @ManyToOne
    private Justification justification;

    @ManyToOne
    private User controleur;

    @ManyToOne
    private Seance seance;

    @ManyToOne
    private User chefEquipe;

    @ManyToOne
    private User chargeMission;

    @ManyToOne
    private Equipe equipe;

    @ManyToOne
    private Mission mission;

    // ===================== INFORMATIONS =====================

    private String noms;

    private Boolean present = false;

    private Boolean justifie = false;

    @Column(columnDefinition = "TEXT")
    private String observation;

    private Boolean isControle = false;

    private String matricule;

    private String unite;

    private String grade;

    private String sexe;

    // ===================== BIOMETRIE =====================

    @Column(columnDefinition = "TEXT")
    private String fingerprint;

    @Column(columnDefinition = "TEXT")
    private String fingerprint4;

    // ===================== FLAGS =====================

    private Boolean isCmd = false;

    private Boolean isActif = false;
    private Boolean isSync = false;
    private Integer versionSync = 1;

    // ===================== FILE =====================

    private String qrcode;
    private String province;
    private String deviceId;

    @Column(name = "pk_photo", unique = true)
    private String pkPhoto;
    // ===================== TIMESTAMPS =====================

    private LocalDateTime syncedAt;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ===================== LIFECYCLE =====================

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}