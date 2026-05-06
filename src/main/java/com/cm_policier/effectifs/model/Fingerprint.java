package com.cm_policier.effectifs.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fingerprint")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fingerprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uuid", nullable = true)
    private Person person;

    private Integer fingerId;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(columnDefinition = "TEXT")
    private String template;
}