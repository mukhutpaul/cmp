package com.cm_policier.effectifs.dto;

import lombok.*;
import java.util.List;

import com.cm_policier.effectifs.model.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcloadDataDTO {

    private User chefEquipe;
    private User chargeMission;

    private Equipe equipe;
    private Mission mission;

    private List<User> users;

    private List<DetailEquipe> detailEquipes;

    private List<EquipeUnite> equipeUnites;

    // ✅ CE CHAMP MANQUE
    private List<Unite> unites;

    private List<MissionUnite> missionUnites;
    private List<DetailUnite> detailUnites;
}