package com.cm_policier.effectifs.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.Justification;
import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControleResponseDto {

    private UUID id;
    private String uid;

    // ================= RELATIONS =================

    private Policier policier;
    private User controleur;
    private User chefEquipe;
    private User chargeMission;

    private Seance seance;
    private Equipe equipe;
    private Mission mission;
    private Justification justification;

    // ================= INFORMATIONS =================

    private String noms;
    private Boolean present;
    private Boolean justifie;
    private String observation;
    private Boolean isControle;

    private String matricule;
    private String unite;
    private String grade;
    private String sexe;

    // ================= BIOMETRIE =================

    private String fingerprint;
    private String fingerprint4;

    // ================= FLAGS =================

    private Boolean isCmd;
    private Boolean isActif;
    private Boolean isSync;
    private Integer versionSync;

    // ================= FILE =================

    private String qrcode;
    private String province;
    private String deviceId;
    private String pkPhoto;
    private String photoUrl;

    // ================= TIMESTAMPS =================

    private LocalDateTime syncedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}