package com.cm_policier.effectifs.service;

import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.dto.SyncResponseDTO;
import com.cm_policier.effectifs.model.*;
import com.cm_policier.effectifs.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PcLocalSyncService {

    private final UserRepository userRepository;
    private final EquipeRepository equipeRepository;
    private final UniteRepository uniteRepository;
    private final DetailUniteRepository detailUniteRepository;
    private final MissionRepository missionRepository;

    private final DetailEquipeRepository detailEquipeRepository;
    private final EquipeUniteRepository equipeUniteRepository;
    private final MissionUniteRepository missionUniteRepository;

    @Transactional
    public void saveSyncData(SyncResponseDTO data) {

        // ================= USERS =================
        if (data.getUsers() != null) {

            for (User incoming : data.getUsers()) {

                User user = new User();

                // IMPORTANT: on ne force PAS update fragile
                if (incoming.getId() != null &&
                        userRepository.existsById(incoming.getId())) {

                    user.setId(incoming.getId());
                }

                user.setUsername(incoming.getUsername());
                user.setEmail(incoming.getEmail());
                user.setNoms(incoming.getNoms());
                user.setPassword(incoming.getPassword());
                user.setProfile(incoming.getProfile());

                userRepository.save(user);
            }
        }

        // ================= MISSION =================
        if (data.getMission() != null) {
            missionRepository.save(data.getMission());
        }

        // ================= EQUIPE =================
        if (data.getEquipe() != null) {
            equipeRepository.save(data.getEquipe());
        }

        // ================= DETAIL EQUIPE =================
        if (data.getDetailEquipes() != null) {
            detailEquipeRepository.saveAll(data.getDetailEquipes());
        }

        // ================= EQUIPE UNITE =================
        if (data.getEquipeUnites() != null) {
            equipeUniteRepository.saveAll(data.getEquipeUnites());
        }

        // ================= MISSION UNITE =================
        if (data.getMissionUnites() != null) {
            missionUniteRepository.saveAll(data.getMissionUnites());
        }

        // ================= UNITES =================
        if (data.getUnites() != null) {
            for (Unite u : data.getUnites()) {
                uniteRepository.save(u);
            }
        }

        // ================= DETAIL UNITE =================
        if (data.getDetailUnites() != null) {
            detailUniteRepository.saveAll(data.getDetailUnites());
        }

        System.out.println("SYNC OK:");
        System.out.println("USERS = " + (data.getUsers() != null ? data.getUsers().size() : 0));
        System.out.println("UNITES = " + (data.getUnites() != null ? data.getUnites().size() : 0));
        System.out.println("EQUIPE ID = " + (data.getEquipe() != null ? data.getEquipe().getId() : null));
    }
}