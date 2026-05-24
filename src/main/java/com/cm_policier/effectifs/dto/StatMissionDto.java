package com.cm_policier.effectifs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatMissionDto {

    private Long id;
    private String numero;
    private String zone;

    private Long totalPoliciers;
    private Long totalControles;
    private Long presents;
    private Long justifies;
    private Long nonJustifies;
    private Long totalEquipes;

    // ✅ AJOUT
    private Long totalUnites;
}