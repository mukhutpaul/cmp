package com.cm_policier.effectifs.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncStatsDto {

    private Long sessions;

    private Long seances;

    private Long controlesPresence;
     private Long controlesJustifies;

    private Long controlesAbsence;

    private Long documents;

    private Long fichiers;

    private Long total;

    private Boolean seanceActive;
}