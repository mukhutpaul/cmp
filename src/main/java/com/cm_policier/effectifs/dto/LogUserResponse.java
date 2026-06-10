package com.cm_policier.effectifs.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogUserResponse {

    private UUID id;
    private String username;
    private String noms;
    private String action;
    private LocalDateTime createdAt;
}