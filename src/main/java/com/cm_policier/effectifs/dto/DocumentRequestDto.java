package com.cm_policier.effectifs.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequestDto {

    private String title;

    private String description;
}