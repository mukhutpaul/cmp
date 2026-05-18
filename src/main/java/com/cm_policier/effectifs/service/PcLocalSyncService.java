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

            Mission incoming = data.getMission();

            Mission mission;

            if (incoming.getId() != null) {
                mission = missionRepository.findById(incoming.getId())
                        .orElse(new Mission());
            } else {
                mission = new Mission();
            }

            mission.setDateDebut(incoming.getDateDebut());
            mission.setDateFin(incoming.getDateFin());
            mission.setZone(incoming.getZone());
            mission.setNumero(incoming.getNumero());
            mission.setIsActive(incoming.getIsActive());

            // ⚠️ relation User (IMPORTANT)
            if (incoming.getChargeMission() != null &&
                    incoming.getChargeMission().getId() != null) {

                User charge = new User();
                charge.setId(incoming.getChargeMission().getId());

                mission.setChargeMission(charge);
            }

            missionRepository.save(mission);
        }

        // ================= EQUIPE =================
        if (data.getEquipe() != null) {

            Equipe incoming = data.getEquipe();

            Equipe equipe;

            if (incoming.getId() != null) {
                equipe = equipeRepository.findById(incoming.getId())
                        .orElse(new Equipe());
            } else {
                equipe = new Equipe();
            }

            // relations
            equipe.setMission(incoming.getMission());
            equipe.setUser(incoming.getUser());
            equipe.setIsActive(incoming.getIsActive());

            equipeRepository.save(equipe);
        }

        // ================= DETAIL EQUIPE =================
        if (data.getDetailEquipes() != null) {

            for (DetailEquipe incoming : data.getDetailEquipes()) {

                DetailEquipe detail = new DetailEquipe();

                detail.setId(incoming.getId());
                detail.setIsActive(incoming.getIsActive());

                // IMPORTANT: Equipe uniquement par ID
                if (incoming.getEquipe() != null && incoming.getEquipe().getId() != null) {
                    Equipe e = new Equipe();
                    e.setId(incoming.getEquipe().getId());
                    detail.setEquipe(e);
                }

                // IMPORTANT: User uniquement par ID
                if (incoming.getUser() != null && incoming.getUser().getId() != null) {
                    User u = new User();
                    u.setId(incoming.getUser().getId());
                    detail.setUser(u);
                }

                detailEquipeRepository.save(detail);
            }
        }

        // ================= EQUIPE UNITE =================
        if (data.getEquipeUnites() != null) {

            for (EquipeUnite incoming : data.getEquipeUnites()) {

                EquipeUnite eu = new EquipeUnite();

                eu.setId(incoming.getId());
                eu.setIsActive(incoming.getIsActive());

                // Equipe uniquement par ID
                if (incoming.getEquipe() != null && incoming.getEquipe().getId() != null) {
                    Equipe equipe = new Equipe();
                    equipe.setId(incoming.getEquipe().getId());
                    eu.setEquipe(equipe);
                }

                // Unite uniquement par ID
                if (incoming.getUnite() != null && incoming.getUnite().getId() != null) {
                    Unite unite = new Unite();
                    unite.setId(incoming.getUnite().getId());
                    eu.setUnite(unite);
                }

                equipeUniteRepository.save(eu);
            }
        }

        // ================= MISSION UNITE =================
        if (data.getMissionUnites() != null) {

            for (MissionUnite incoming : data.getMissionUnites()) {

                MissionUnite mu = new MissionUnite();

                mu.setId(incoming.getId());
                mu.setIsActive(incoming.getIsActive());

                // Mission uniquement par ID
                if (incoming.getMission() != null && incoming.getMission().getId() != null) {
                    Mission mission = new Mission();
                    mission.setId(incoming.getMission().getId());
                    mu.setMission(mission);
                }

                // Unite uniquement par ID
                if (incoming.getUnite() != null && incoming.getUnite().getId() != null) {
                    Unite unite = new Unite();
                    unite.setId(incoming.getUnite().getId());
                    mu.setUnite(unite);
                }

                missionUniteRepository.save(mu);
            }
        }

        // ================= UNITES =================
        if (data.getUnites() != null) {

            for (Unite incoming : data.getUnites()) {

                Unite unite = new Unite();

                unite.setId(incoming.getId());
                unite.setName(incoming.getName());
                unite.setSignature(incoming.getSignature());
                unite.setEquipeaf(incoming.getEquipeaf());

                // IMPORTANT: commandant uniquement par ID
                if (incoming.getCommandant() != null && incoming.getCommandant().getIdPersonnel() != null) {

                    Person p = new Person();
                    p.setIdPersonnel(incoming.getCommandant().getIdPersonnel());

                    unite.setCommandant(p);
                }

                uniteRepository.save(unite);
            }
        }

        // ================= DETAIL UNITE =================
        if (data.getDetailUnites() != null) {

            for (DetailUnite du : data.getDetailUnites()) {

                DetailUnite entity;

                if (du.getId() != null) {
                    entity = detailUniteRepository.findById(du.getId())
                            .orElse(new DetailUnite());
                } else {
                    entity = new DetailUnite();
                }

                // IMPORTANT: on ne garde PAS les objets attachés du serveur
                entity.setUser(
                        du.getUser() != null ? userRepository.findById(du.getUser().getId()).orElse(null) : null);
                entity.setUnite(
                        du.getUnite() != null ? uniteRepository.findById(du.getUnite().getId()).orElse(null) : null);

                entity.setIsActive(du.getIsActive());

                detailUniteRepository.save(entity);
            }
        }

        System.out.println("SYNC OK:");
        System.out.println("USERS = " + (data.getUsers() != null ? data.getUsers().size() : 0));
        System.out.println("UNITES = " + (data.getUnites() != null ? data.getUnites().size() : 0));
        System.out.println("EQUIPE ID = " + (data.getEquipe() != null ? data.getEquipe().getId() : null));
    }
}