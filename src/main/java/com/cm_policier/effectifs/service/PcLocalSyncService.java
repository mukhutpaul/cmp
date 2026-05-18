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

        // ================= USERS (UPSERT PROPRE) =================
        if (data.getUsers() != null) {

            for (User incoming : data.getUsers()) {

                User user = userRepository.findById(incoming.getId())
                        .orElseGet(User::new);

                user.setId(incoming.getId());
                user.setUsername(incoming.getUsername());
                user.setEmail(incoming.getEmail());
                user.setNoms(incoming.getNoms());
                user.setPassword(incoming.getPassword());

                userRepository.save(user);
            }
        }

        // ================= MISSION =================
        if (data.getMission() != null) {

            Mission incoming = data.getMission();

            Mission mission = missionRepository.findById(incoming.getId())
                    .orElseGet(Mission::new);

            mission.setId(incoming.getId());
            mission.setChargeMission(incoming.getChargeMission());


            missionRepository.save(mission);
        }

        // ================= EQUIPE =================
        if (data.getEquipe() != null) {

            Equipe incoming = data.getEquipe();

            Equipe equipe = equipeRepository.findById(incoming.getId())
                    .orElseGet(Equipe::new);

            equipe.setId(incoming.getId());
            equipe.setUser(incoming.getUser());

            equipeRepository.save(equipe);
        }

        // ================= UNITES =================
        if (data.getUnites() != null) {

            for (Unite u : data.getUnites()) {

                Unite unite = uniteRepository.findById(u.getId())
                        .orElseGet(Unite::new);

                unite.setId(u.getId());
                unite.setName(u.getName());

                uniteRepository.save(unite);
            }
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

        // ================= DETAIL UNITE =================
        if (data.getDetailUnites() != null) {
            detailUniteRepository.saveAll(data.getDetailUnites());
        }

        System.out.println("SYNC OK");
    }
}