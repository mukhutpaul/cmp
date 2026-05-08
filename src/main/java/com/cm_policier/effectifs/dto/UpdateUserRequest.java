package com.cm_policier.effectifs.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String username;
    private String email;
    private String noms;
    private String password;

    // IMPORTANT: Long et non String
    private Long profileId;
}