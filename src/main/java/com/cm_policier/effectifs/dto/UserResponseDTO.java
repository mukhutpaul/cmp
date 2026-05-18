package com.cm_policier.effectifs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String noms;
}