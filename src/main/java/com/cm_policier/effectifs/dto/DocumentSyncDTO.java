package com.cm_policier.effectifs.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentSyncDTO {

    private UUID id;
    private UUID controleId;
    private String title;
    private String imageBase64;
}