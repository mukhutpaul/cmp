package com.cm_policier.effectifs.dto;

import java.util.List;

import com.cm_policier.effectifs.model.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncPayloadDTO {

    private User chefEquipe;

    private Mission mission;

    private Equipe equipe;

    private List<User> users;

    private List<DetailUnite> unites;
}