package com.cm_policier.effectifs.dto;
import java.util.UUID;

import lombok.Data;

@Data
public class SeanceRequest {

    private Long missionId;
    private Long chefEquipeId;
}