package com.cm_policier.effectifs.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MobileLoginRequest {
    private String username;
    private String password;
}
