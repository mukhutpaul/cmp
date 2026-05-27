package com.cm_policier.effectifs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private LocalDate dateSession;

    private LocalTime heureDebut;

    private LocalTime heureFin;

    @ManyToOne
    private User controleur;

    @ManyToOne
    private Seance seance;

    private Boolean isSynchronized = false;

    private Boolean isActive = false;
     private Integer versionSync = 1;

}