package com.cm_policier.effectifs.dto;


import lombok.*;

@Getter
@Setter
@Builder
public class SyncBatchResponse {

    private Integer sessionsSynced;

    private Integer seancesSynced;

    private Integer controlesSynced;

    private Integer documentsSynced;

    private Integer filesSaved;

    private String status;

    private Long durationMs;
}