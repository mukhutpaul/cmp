package com.cm_policier.effectifs.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.SyncPayloadDTO;
import com.cm_policier.effectifs.model.*;
import com.cm_policier.effectifs.service.*;

@RestController
@RequestMapping("/api/pc")
@CrossOrigin("*")
public class PcSyncController {

    @Autowired
    private UserService userService;

    @Autowired
    private EquipeService equipeService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private DetailUniteService detailUniteService;

    @PostMapping("/sync/{chefId}")
    public ResponseEntity<?> syncData(
            @PathVariable Long chefId
    ) {

        try {

            // =========================
            // 1. CHEF
            // =========================
            User chef = userService.findById(chefId);

            if (chef == null) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Chef introuvable", null));
            }

            // =========================
            // 2. EQUIPE
            // =========================
            Equipe equipe = equipeService.findByChef(chefId);

            // =========================
            // 3. USERS DE L’EQUIPE
            // =========================
            List<User> users = userService.findUsersByEquipe(equipe.getId());

            // =========================
            // 4. MISSION
            // =========================
            Mission mission = missionService.findMissionByEquipe(equipe.getId());

            // =========================
            // 5. UNITES DES CONTROLEURS
            // =========================
            List<DetailUnite> unites =
                    detailUniteService.findUnitesByEquipe(equipe.getId());

            // =========================
            // 6. PAYLOAD
            // =========================
            SyncPayloadDTO payload = SyncPayloadDTO.builder()
                    .chefEquipe(chef)
                    .equipe(equipe)
                    .mission(mission)
                    .users(users)
                    .unites(unites)
                    .build();

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Synchronisation réussie", payload));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>(false, "Erreur sync", e.getMessage()));
        }
    }
}