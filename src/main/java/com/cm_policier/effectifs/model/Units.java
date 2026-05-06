package com.cm_policier.effectifs.model;

import jakarta.persistence.*;

@Entity
@Table(name = "units")
public class Units {

    @Id
    private Integer id;

    private String unit;
    private String battalion;

    @ManyToOne
    @JoinColumn(name = "Id_Garnison_units")
    private Garnison garnison;

    private Boolean valide;
}