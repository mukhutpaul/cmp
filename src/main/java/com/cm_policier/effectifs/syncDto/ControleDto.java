package com.cm_policier.effectifs.syncDto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControleDto {

    private UUID id;

    private String uid;

    private String policierId;

    private String justificationId;

    private String controleurId;

    private String chefEquipeId;

    private String chargeMissionId;

    private String equipeId;

    private String missionId;

    // ===================== INFOS METIER =====================

    private String noms;

    private Boolean present;

    private Boolean justifie;

    private String observation;

    private Boolean isControle;

    private String matricule;

    private String unite;

    private String grade;

    private String sexe;

    // ===================== BIOMETRIE =====================

    private String fingerprint;

    private String fingerprint4;

    // ===================== SYNC =====================

    private Boolean isSync;

    private Integer versionSync;

    private LocalDateTime lastModified;

    private String deviceId;

    private String pkPhoto;

    // ===================== SEANCE =====================

    private UUID seanceId;

    // ===================== STATUS (important pour sync) =====================

    private Boolean deleted;

    private String syncStatus;
}