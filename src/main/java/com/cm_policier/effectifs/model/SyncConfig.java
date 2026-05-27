package com.cm_policier.effectifs.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Exemple:
     * http://10.10.10.10:8090
     */
    private String serverAddress;

    /**
     * Nombre max de données par batch
     */
    private Integer batchSize = 100;

    /**
     * Timeout réseau
     */
    private Integer timeoutSeconds = 60;

    /**
     * Compression images
     */
    private Boolean compressionEnabled = true;

    /**
     * Retry automatique
     */
    private Boolean autoRetry = true;

    /**
     * Synchronisation automatique
     */
    private Boolean autoSync = false;
}