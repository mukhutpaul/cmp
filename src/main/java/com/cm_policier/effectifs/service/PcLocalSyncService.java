package com.cm_policier.effectifs.service;

import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.dto.SyncResponseDTO;
import com.cm_policier.effectifs.model.DetailEquipe;
import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.EquipeUnite;
import com.cm_policier.effectifs.model.MissionUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.DetailEquipeRepository;
import com.cm_policier.effectifs.repository.DetailUniteRepository;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.repository.EquipeUniteRepository;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.repository.MissionUniteRepository;
import com.cm_policier.effectifs.repository.UniteRepository;
import com.cm_policier.effectifs.repository.UserRepository;

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
            for (User u : data.getUsers()) {
                userRepository.save(u);
            }
        }

        // ================= EQUIPE =================
        if (data.getEquipe() != null) {
            equipeRepository.save(data.getEquipe());
        }

        // ================= MISSION =================
        if (data.getMission() != null) {
            missionRepository.save(data.getMission());
        }

        // ================= UNITES =================
        if (data.getUnites() != null) {
            for (Unite u : data.getUnites()) {
                uniteRepository.save(u);
            }
        }

        // ================= DETAIL UNITE =================
        if (data.getDetailUnites() != null) {
            for (DetailUnite du : data.getDetailUnites()) {
                detailUniteRepository.save(du);
            }
        }

        // ================= DETAIL EQUIPE =================
        if (data.getDetailEquipes() != null) {
            for (DetailEquipe de : data.getDetailEquipes()) {
                detailEquipeRepository.save(de);
            }
        }

        // ================= EQUIPE-UNITE =================
        if (data.getEquipeUnites() != null) {
            for (EquipeUnite eu : data.getEquipeUnites()) {
                equipeUniteRepository.save(eu);
            }
        }

        // ================= MISSION-UNITE =================
        if (data.getMissionUnites() != null) {
            for (MissionUnite mu : data.getMissionUnites()) {
                missionUniteRepository.save(mu);
            }
        }
    }
}