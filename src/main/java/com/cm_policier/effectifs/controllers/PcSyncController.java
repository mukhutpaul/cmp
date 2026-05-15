package com.cm_policier.effectifs.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.PcSyncLoginDTO;
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

    @PostMapping("/sync")
    public ResponseEntity<?> syncData(
            @RequestBody PcSyncLoginDTO request
    ) {
        try {
            // ================= AUTH =================

            User chef = userService.login(
                    request.getUsername(),
                    request.getPassword()
            );

            if (chef == null) {

                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Username ou mot de passe incorrect",
                                null
                        ));
            }

            // ================= EQUIPE =================

            Equipe equipe = equipeService.findByChef(chef.getId());

            if (equipe == null) {

                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Equipe introuvable",
                                null
                        ));
            }

            // ================= DATA =================

            List<User> users =
                    userService.findUsersByEquipe(equipe.getId());

            Mission mission =
                    missionService.findMissionByUser(chef.getId());

            List<DetailUnite> unites =
                    detailUniteService.findUnitesByEquipe(equipe.getId());

            SyncPayloadDTO payload = SyncPayloadDTO.builder()
                    .chefEquipe(chef)
                    .equipe(equipe)
                    .mission(mission)
                    .users(users)
                    .unites(unites)
                    .build();

            // ================= SAVE LOCAL =================

            syncService.saveSyncData(payload);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Synchronisation réussie",
                            payload
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(
                            false,
                            "Erreur sync",
                            e.getMessage()
                    ));
        }
    }
}