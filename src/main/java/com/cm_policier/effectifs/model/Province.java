package com.cm_policier.effectifs.model;

import jakarta.persistence.*;

@Entity
@Table(name = "origin_province")
public class Province {

    @Id
    private Integer id;

    private String province;

    @Column(name = "Code_Prov")
    private String codeProv;

    private String intituleprovince;
}
