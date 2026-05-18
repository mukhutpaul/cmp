package com.cm_policier.effectifs.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.PcSyncLoginDTO;
import com.cm_policier.effectifs.dto.SyncResponseDTO;
import com.cm_policier.effectifs.model.*;
import com.cm_policier.effectifs.service.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pc")
@RequiredArgsConstructor
public class PcLoadDataController {

    private final PcLocalSyncService syncService;

    private final UserService userService;
    private final EquipeService equipeService;
    private final DetailUniteService detailUniteService;
    private final DetailEquipeService detailEquipeService;
    private final EquipeUniteService equipeUniteService;
    private final MissionUniteService missionUniteService;

    @PostMapping("/sync")
    public ResponseEntity<?> syncData(@RequestBody PcSyncLoginDTO request) {

        try {

            System.out.println("\n========== SYNC START ==========");

            // ================= AUTH =================
            User chef = userService.login(
                    request.getUsername(),
                    request.getPassword());

            if (chef == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false,
                                "Username ou mot de passe incorrect",
                                null));
            }

            System.out.println("CHEF = " + chef.getUsername());

            // ================= EQUIPE =================
            Equipe equipe = equipeService.findByChef(chef.getId());

            if (equipe == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false,
                                "Equipe introuvable",
                                null));
            }

            Mission mission = equipe.getMission();

            // ================= USERS =================
            List<User> users = new ArrayList<>(userService.findUsersByEquipe(equipe.getId()));
            users.add(chef);

            User chargeMission = null;

            if (mission != null && mission.getChargeMission() != null) {
                chargeMission = userService.findById(mission.getChargeMission().getId());
                if (chargeMission != null) {
                    users.add(chargeMission);
                }
            }

            // ================= UNITES =================
            Set<Unite> uniteSet = new HashSet<>();

            List<EquipeUnite> equipeUnites = equipeUniteService.findByEquipe(equipe.getId());
            if (equipeUnites != null) {
                for (EquipeUnite eu : equipeUnites) {
                    if (eu.getUnite() != null) {
                        uniteSet.add(eu.getUnite());
                    }
                }
            }

            List<MissionUnite> missionUnites = (mission != null)
                    ? missionUniteService.findByMission(mission.getId())
                    : new ArrayList<>();

            if (missionUnites != null) {
                for (MissionUnite mu : missionUnites) {
                    if (mu.getUnite() != null) {
                        uniteSet.add(mu.getUnite());
                    }
                }
            }

            List<DetailUnite> detailUnites = detailUniteService.findUnitesByEquipe(equipe.getId());

            if (detailUnites != null) {
                for (DetailUnite du : detailUnites) {
                    if (du.getUnite() != null) {
                        uniteSet.add(du.getUnite());
                    }
                }
            }

            List<Unite> unites = new ArrayList<>(uniteSet);

            // ================= DETAIL EQUIPE =================
            List<DetailEquipe> detailEquipes = detailEquipeService.findByEquipe(equipe.getId());

            // ================= PAYLOAD FINAL (SYNC DTO) =================
            SyncResponseDTO payload = new SyncResponseDTO();

            payload.setChefEquipe(chef);
            payload.setChargeMission(chargeMission);
            payload.setEquipe(equipe);
            payload.setMission(mission);
            payload.setUsers(users);
            payload.setUnites(unites);
            payload.setDetailEquipes(detailEquipes);
            payload.setEquipeUnites(equipeUnites);
            payload.setMissionUnites(missionUnites);
            payload.setDetailUnites(detailUnites);

            // ================= SAVE LOCAL =================
            //syncService.saveSyncData(payload);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Synchronisation réussie",
                            payload));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false,
                            "Erreur sync",
                            e.getMessage()));
        }
    }
}