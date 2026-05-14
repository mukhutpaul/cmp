package com.cm_policier.effectifs.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.SyncPayloadDTO;
import com.cm_policier.effectifs.model.*;
import com.cm_policier.effectifs.service.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pc")
@RequiredArgsConstructor
public class PcSyncController {

    private final PcLocalSyncService syncService;

    private final UserService userService;
    private final EquipeService equipeService;
    private final MissionService missionService;
    private final DetailUniteService detailUniteService;

    @PostMapping("/sync/{chefId}")
    public ResponseEntity<?> syncData(@PathVariable Long chefId) {

        try {

            User chef = userService.findById(chefId);

            if (chef == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Chef introuvable", null));
            }

            Equipe equipe = equipeService.findByChef(chefId);

            if (equipe == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Equipe introuvable", null));
            }

            List<User> users = userService.findUsersByEquipe(equipe.getId());
            Mission mission = missionService.findMissionByUser(chefId);
            List<DetailUnite> unites = detailUniteService.findUnitesByEquipe(equipe.getId());

            SyncPayloadDTO payload = SyncPayloadDTO.builder()
                    .chefEquipe(chef)
                    .equipe(equipe)
                    .mission(mission)
                    .users(users)
                    .unites(unites)
                    .build();

            // 🔥 SAVE LOCAL DB
            syncService.saveSyncData(payload);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Sync + Save local réussi", payload)
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Erreur sync", e.getMessage()));
        }
    }
}