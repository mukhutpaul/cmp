package com.cm_policier.effectifs.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "controle_id")
    private Controle controle;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl; // chemin image
}
