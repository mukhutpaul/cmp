package com.cm_policier.effectifs.syncDto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeanceDto {

    private UUID id;

    /**
     * Date début séance
     */
    private LocalDateTime dateSeance;

    /**
     * Date fin séance
     */
    private LocalDateTime dateFin;

    /**
     * Références légères (pas d'entités JPA)
     */
    private String chefEquipeId;

    private String missionId;

    /**
     * État métier
     */
    private Boolean isActive;

    /**
     * Sync control
     */
    private Integer versionSync;

    private Boolean isSynchronized;

    private LocalDateTime syncedAt;

    private LocalDateTime updatedAt;

    /**
     * Device source (tablette/PC)
     */
    private String deviceId;

    /**
     * Sécurité sync (optionnel mais utile)
     */
    private Boolean deleted;
}