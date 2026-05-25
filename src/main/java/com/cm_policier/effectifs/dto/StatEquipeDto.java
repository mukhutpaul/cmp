package com.cm_policier.effectifs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatEquipeDto {

    private Long equipeId;

    private String equipe;

    private String missionNumero;

    private String zone;

    private Long totalPoliciers;

    private Long totalControles;

    private Long presents;

    private Long justifies;

    private Long nonJustifies;

    private Long totalUnites;
}