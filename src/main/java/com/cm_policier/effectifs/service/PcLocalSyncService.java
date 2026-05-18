package com.cm_policier.effectifs.service;

import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.PcloadDataDTO;
import com.cm_policier.effectifs.dto.SyncResponseDTO;
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
    public void saveSyncData(PcloadDataDTO data) {

        // ================= USERS =================
        // ================= USERS =================
        if (data.getUsers() != null) {

            for (User incomingUser : data.getUsers()) {

                User userToSave;

                // utilisateur existe déjà
                if (incomingUser.getId() != null &&
                        userRepository.existsById(incomingUser.getId())) {

                    User existingUser = userRepository
                            .findById(incomingUser.getId())
                            .orElse(null);

                    if (existingUser != null) {

                        // garder ancien password si null
                        if (incomingUser.getPassword() == null ||
                                incomingUser.getPassword().isBlank()) {

                            incomingUser.setPassword(
                                    existingUser.getPassword());
                        }

                        userToSave = incomingUser;

                    } else {
                        userToSave = incomingUser;
                    }

                } else {

                    // nouvel utilisateur
                    userToSave = incomingUser;
                }

                System.out.println(
                        "SAVE USER => "
                                + userToSave.getUsername()
                                + " PASSWORD = "
                                + userToSave.getPassword());

                userRepository.saveAndFlush(userToSave);
            }
        }

        // ================= MISSION =================
        if (data.getMission() != null) {
            missionRepository.saveAndFlush(data.getMission());
        }

        // ================= EQUIPE =================
        if (data.getEquipe() != null) {
            equipeRepository.saveAndFlush(data.getEquipe());
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
                uniteRepository.saveAndFlush(u);
            }
        }

        // ================= DETAIL UNITE =================
        if (data.getDetailUnites() != null) {
            detailUniteRepository.saveAll(data.getDetailUnites());
        }
        System.out.println("USERS SAVED: " + data.getUsers().size());
        System.out.println("UNITES SAVED: " + data.getUnites().size());
        System.out.println("EQUIPE: " + data.getEquipe().getId());
    }

}