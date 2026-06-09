package com.cm_policier.effectifs.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControleStatsDto {

    private long totalControles;

    private long totalPresent;
    private long totalJustifie;

    private long totalHommesPresent;
    private long totalFemmesPresent;

    private long totalHommesJustifies;
    private long totalFemmesJustifies;

    private long totalGlobalPresentEtJustifie;

    private long totalUnites;

    private Map<String, Long> statsParUnite;
    private Map<String, Long> resteParUnite;
}