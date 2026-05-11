package com.cm_policier.effectifs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {

    private long totalPoliciers;
    private long totalUnites;
    private long totalEquipes;
    private long totalMissions;

    private long totalControles;
    private long totalPresent;
    private long totalJustifies;
    private long totalNonJustifies;
}