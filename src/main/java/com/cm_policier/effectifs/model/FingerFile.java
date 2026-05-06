package com.cm_policier.effectifs.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FingerFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeFile;

    private String fileName;

    private LocalDateTime uploaded;

    private Boolean isActive = false;

    @PrePersist
    public void prePersist() {
        this.uploaded = LocalDateTime.now();
    }
}