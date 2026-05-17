package com.cm_policier.effectifs.dto;

import java.util.List;

import com.cm_policier.effectifs.model.DetailEquipe;
import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.EquipeUnite;
import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.MissionUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.model.User;

import lombok.Data;

@Data
public class SyncResponseDTO {

    private User chefEquipe;
    private User chargeMission;

    private Equipe equipe;
    private Mission mission;

    private List<User> users;

    private List<Unite> unites;

    private List<DetailUnite> detailUnites;

    private List<EquipeUnite> equipeUnites;

    private List<MissionUnite> missionUnites;

    private List<DetailEquipe> detailEquipes;

}
