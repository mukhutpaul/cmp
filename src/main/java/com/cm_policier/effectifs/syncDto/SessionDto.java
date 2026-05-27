package com.cm_policier.effectifs.syncDto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDto {

    private UUID id;

    /**
     * Date de la session
     */
    private LocalDate dateSession;

    /**
     * Heure début session
     */
    private LocalTime heureDebut;

    /**
     * Heure fin session
     */
    private LocalTime heureFin;

    /**
     * Références légères (évite relations JPA)
     */
    private String controleurId;

    private UUID seanceId;

    /**
     * État métier
     */
    private Boolean isActive;

    /**
     * Sync control
     */
    private Boolean isSynchronized;

    private LocalDateTime syncedAt;

    private LocalDateTime updatedAt;

    /**
     * Sync version (important pour conflit)
     */
    private Integer versionSync;

    /**
     * Device source (tablette / PC)
     */
    private String deviceId;

    /**
     * Soft delete optionnel
     */
    private Boolean deleted;
}