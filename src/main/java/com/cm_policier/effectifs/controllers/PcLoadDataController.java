package com.cm_policier.effectifs.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.PcSyncLoginDTO;
import com.cm_policier.effectifs.dto.PcloadDataDTO;
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
    public ResponseEntity<?> syncData(
            @RequestBody PcSyncLoginDTO request) {

        try {

            System.out.println("\n========== SYNC START ==========");

            // ================= AUTH =================
            User chef = userService.login(
                    request.getUsername(),
                    request.getPassword());

            if (chef == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Username ou mot de passe incorrect",
                                null));
            }

            System.out.println("CHEF = " + chef.getUsername());

            // ================= EQUIPE =================
            Equipe equipe = equipeService.findByChef(chef.getId());

            if (equipe == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Equipe introuvable",
                                null));
            }

            System.out.println("EQUIPE ID = " + equipe.getId());

            // ================= MISSION =================
            Mission mission = equipe.getMission();

            // ================= USERS GROUPÉS =================
            Set<User> usersSet = new HashSet<>();
            List<User> equipeUsers = userService.findUsersByEquipe(equipe.getId());

            List<User> fullUsers = new ArrayList<>();

            if (equipeUsers != null) {

                for (User u : equipeUsers) {

                    // ✅ récupérer user COMPLET avec password
                    User fullUser = userService.findFullById(u.getId());

                    fullUsers.add(fullUser);

                    System.out.println("========== USER ==========");
                    System.out.println("ID = " + fullUser.getId());
                    System.out.println("USERNAME = " + fullUser.getUsername());
                    System.out.println("PASSWORD = " + fullUser.getPassword());
                    System.out.println("--------------------------");
                }
            }

            equipeUsers = fullUsers;

            if (equipeUsers != null) {
                usersSet.addAll(equipeUsers);
            }

            usersSet.add(chef);

            User chargeMission = null;

            if (mission != null && mission.getChargeMission() != null) {
                chargeMission = userService.findById(
                        mission.getChargeMission().getId());

                if (chargeMission != null) {
                    usersSet.add(chargeMission);
                }
            }

            List<User> users = new ArrayList<>(usersSet);

            // ================= UNITES GROUPÉES =================
            Set<Unite> uniteSet = new HashSet<>();

            List<EquipeUnite> equipeUnites = equipeUniteService.findByEquipe(equipe.getId());

            if (equipeUnites != null) {
                for (EquipeUnite eu : equipeUnites) {
                    if (eu.getUnite() != null) {
                        uniteSet.add(eu.getUnite());
                    }
                }
            }

            List<MissionUnite> missionUnites = missionUniteService.findByMission(
                    mission != null ? mission.getId() : null);

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

            // ================= RELATIONS =================
            List<DetailEquipe> detailEquipes = detailEquipeService.findByEquipe(equipe.getId());

            System.out.println("DETAIL EQUIPE SIZE = " + detailEquipes.size());

            System.out.println("EQUIPE UNITES SIZE = " + equipeUnites.size());
            System.out.println("MISSION UNITES SIZE = " + missionUnites.size());
            System.out.println("DETAIL UNITES SIZE = " + detailUnites.size());

            // ================= PAYLOAD =================
            PcloadDataDTO payload = PcloadDataDTO.builder()
                    .chefEquipe(chef)
                    .chargeMission(chargeMission)
                    .equipe(equipe)
                    .mission(mission)

                    // USERS
                    .users(users)

                    // UNITES GROUPÉES
                    .unites(unites)

                    // RELATIONS CONSERVÉES
                    .detailEquipes(detailEquipes)
                    .equipeUnites(equipeUnites)
                    .detailUnites(detailUnites)
                    .missionUnites(missionUnites)

                    .build();

            // ================= SAVE =================
            syncService.saveSyncData(payload);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Synchronisation réussie",
                            payload));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(
                            false,
                            "Erreur sync",
                            e.getMessage()));
        }
    }
}