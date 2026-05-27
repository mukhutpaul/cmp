package com.cm_policier.effectifs.syncDto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {

    private UUID id;

    /**
     * Référence métier du contrôle
     * (important pour rattacher le document)
     */
    private String controleUid;

    private String title;

    private String description;

    /**
     * Nom du fichier image côté client
     * ex: photo1.jpg
     */
    private String imageUrl;

    /**
     * Nom final côté serveur (après rename)
     * ex: UID_xxx.jpg
     */
    private String serverFileName;

    /**
     * Version de sync pour éviter conflits
     */
    private Integer versionSync;

    /**
     * Device source (tablette/PC)
     */
    private String deviceId;

    private Boolean IsSync;
    private LocalDateTime syncedAt;

  

}