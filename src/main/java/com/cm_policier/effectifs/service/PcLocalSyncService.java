package com.cm_policier.effectifs.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.SyncPayloadDTO;
import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.model.User;
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
        if (payload.getUsers() != null && !payload.getUsers().isEmpty()) {

            List<User> safeUsers = new ArrayList<>();

            for (User u : payload.getUsers()) {

                if (u == null) continue;

                User user = userRepository.findById(u.getId())
                        .orElse(new User());

                user.setId(u.getId());
                user.setUsername(u.getUsername());
                user.setEmail(u.getEmail());
                user.setNoms(u.getNoms());

                // 🔥 IMPORTANT : password sync
                user.setPassword(u.getPassword());

                user.setProfile(u.getProfile());

                safeUsers.add(user);
            }

            userRepository.saveAll(safeUsers);
        }

        // ================= EQUIPE =================
        if (payload.getEquipe() != null) {
            equipeRepository.save(payload.getEquipe());
        }

        // ================= MISSION =================
        if (payload.getMission() != null) {
            missionRepository.save(payload.getMission());
        }

        // ================= DETAIL UNITES =================
        if (payload.getUnites() != null && !payload.getUnites().isEmpty()) {
            detailUniteRepository.saveAll(payload.getUnites());
        }

        // ================= UNITES =================
        List<Unite> uniteList = new ArrayList<>();

        if (payload.getUnites() != null) {

            for (DetailUnite detail : payload.getUnites()) {

                if (detail != null && detail.getUnite() != null) {

                    Long uniteId = detail.getUnite().getId();

                    if (uniteId != null) {

                        Unite unite = uniteRepository.findById(uniteId)
                                .orElse(new Unite());

                        unite.setId(uniteId);
                        unite.setName(detail.getUnite().getName());
                        unite.setCommandant(detail.getUnite().getCommandant());
                        unite.setSignature(detail.getUnite().getSignature());
                        unite.setEquipeaf(detail.getUnite().getEquipeaf());

                        uniteList.add(unite);
                    }
                }
            }
        }

        if (!uniteList.isEmpty()) {
            uniteRepository.saveAll(uniteList);
        }
    }
}