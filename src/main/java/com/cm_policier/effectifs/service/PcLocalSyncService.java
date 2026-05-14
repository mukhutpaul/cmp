package com.cm_policier.effectifs.service;

import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.SyncPayloadDTO;
import com.cm_policier.effectifs.repository.DetailUniteRepository;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.repository.UniteRepository;
import com.cm_policier.effectifs.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PcLocalSyncService {

    private final UserRepository userRepository;
    private final EquipeRepository equipeRepository;
    private final MissionRepository missionRepository;
    private final DetailUniteRepository detailUniteRepository;
    private final UniteRepository uniteRepository;

    public void saveSyncData(SyncPayloadDTO payload) {

        // ================= USERS =================
        userRepository.saveAll(payload.getUsers());
        

        // ================= EQUIPE =================
        equipeRepository.save(payload.getEquipe());

        // ================= MISSION =================
        missionRepository.save(payload.getMission());

        // ================= UNITES =================
        detailUniteRepository.saveAll(payload.getUnites());

       // uniteRepository.saveAll(payload.getUnites());
    }
}