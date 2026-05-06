package com.cm_policier.effectifs.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Garnison")
public class Garnison {

    @Id
    @Column(name = "Id_Garnison")
    private Integer id;

    @Column(name = "Libelle_Garnison")
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "Id_province_Garnion")
    private Province province;
}