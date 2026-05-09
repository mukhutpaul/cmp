package com.cm_policier.effectifs.dto;


public record ChargerUniteRequest(
    Long uniteId,
    Long missionId,
    Long equipeId,
    Long userId
) {
}