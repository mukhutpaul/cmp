package com.cm_policier.effectifs.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "face")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Face {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uuid", nullable = true)
    private Person person;

    @Column(columnDefinition = "TEXT")
    private String data;
}