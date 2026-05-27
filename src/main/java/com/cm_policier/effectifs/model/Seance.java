package com.cm_policier.effectifs.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private LocalDateTime dateSeance;

    private LocalDateTime dateFin;

    @ManyToOne
    private User chefEquipe;

    @ManyToOne
    private Mission mission;

    private Boolean isActive = false;

    private Integer versionSync = 1;

    private LocalDateTime syncedAt;

    private LocalDateTime updatedAt;

    private Boolean isSynchronized = false;


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}