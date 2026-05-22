package com.cm_policier.effectifs.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControleResponseDto {

    // ===================== IDENTIFIANTS =====================

    private UUID id;
    private String uid;

    // ===================== RELATIONS =====================

    private Long policierId;

    private UUID justificationId;

    private Long controleurId;

    private UUID seanceId;

    private Long chefEquipeId;

    private Long chargeMissionId;

    private Long equipeId;

    private Long missionId;

    // ===================== INFORMATIONS =====================

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

    // ===================== FLAGS =====================

    private Boolean isCmd;

    private Boolean isActif;

    private Boolean isSync;

    private Integer versionSync;

    // ===================== FILE =====================

    private String qrcode;

    private String province;

    private String deviceId;

    private String pkPhoto;

    private String photoUrl;

    // ===================== TIMESTAMPS =====================

    private LocalDateTime syncedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}