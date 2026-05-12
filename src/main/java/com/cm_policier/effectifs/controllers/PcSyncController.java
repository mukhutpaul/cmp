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
        public ResponseEntity<?> syncData(@PathVariable Long chefId) {

                try {

                        User chef = userService.findById(chefId);

                        if (chef == null) {
                                return ResponseEntity.badRequest().body(
                                                new ApiResponse<>(false, "Chef introuvable", null));
                        }

                        Equipe equipe = equipeService.findByChef(chefId);

                        if (equipe == null) {
                                return ResponseEntity.badRequest().body(
                                                new ApiResponse<>(false, "Equipe introuvable", null));
                        }

                        // USERS
                        List<User> users = userService.findUsersByEquipe(equipe.getId());

                        // MISSION (corrigé)
                        Mission mission = missionService.findMissionByUser(chefId);

                        // UNITES
                        List<DetailUnite> unites = detailUniteService.findUnitesByEquipe(equipe.getId());

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