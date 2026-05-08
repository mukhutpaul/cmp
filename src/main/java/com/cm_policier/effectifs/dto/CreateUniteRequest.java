package com.cm_policier.effectifs.dto;
import lombok.Data;

@Data
public class CreateUniteRequest {

    private String name;

    private String commandantId;

    private String signature;
}
