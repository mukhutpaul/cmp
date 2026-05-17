package com.cm_policier.effectifs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PcSyncLoginDTO {

    private String username;
    private String password;

    private String baseUrl;
}