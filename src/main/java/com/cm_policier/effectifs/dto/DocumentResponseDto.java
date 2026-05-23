package com.cm_policier.effectifs.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentResponseDto {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
}