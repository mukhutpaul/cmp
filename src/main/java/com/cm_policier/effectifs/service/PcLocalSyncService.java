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

    // ================= USERS (1) =================
    if (data.getUsers() != null) {

        for (User incoming : data.getUsers()) {

            User user = new User();

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

    // ================= UNITES (2) =================
    if (data.getUnites() != null) {

        for (Unite incoming : data.getUnites()) {

            Unite unite = new Unite();

            unite.setId(incoming.getId());
            unite.setName(incoming.getName());
            unite.setSignature(incoming.getSignature());
            unite.setEquipeaf(incoming.getEquipeaf());

            if (incoming.getCommandant() != null &&
                    incoming.getCommandant().getIdPersonnel() != null) {

                Person p = new Person();
                p.setIdPersonnel(incoming.getCommandant().getIdPersonnel());

                unite.setCommandant(p);
            }

            uniteRepository.save(unite);
        }
    }

    // ================= MISSION (3) =================
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

        if (incoming.getChargeMission() != null &&
                incoming.getChargeMission().getId() != null) {

            User charge = new User();
            charge.setId(incoming.getChargeMission().getId());

            mission.setChargeMission(charge);
        }

        missionRepository.save(mission);
    }

    // ================= EQUIPE (4) =================
    if (data.getEquipe() != null) {

        Equipe incoming = data.getEquipe();

        Equipe equipe;

        if (incoming.getId() != null) {
            equipe = equipeRepository.findById(incoming.getId())
                    .orElse(new Equipe());
        } else {
            equipe = new Equipe();
        }

        equipe.setMission(incoming.getMission());
        equipe.setUser(incoming.getUser());
        equipe.setIsActive(incoming.getIsActive());

        equipeRepository.save(equipe);
    }

    // ================= DETAIL EQUIPE (5) =================
    if (data.getDetailEquipes() != null) {

        for (DetailEquipe incoming : data.getDetailEquipes()) {

            DetailEquipe detail = new DetailEquipe();

            detail.setId(incoming.getId());
            detail.setIsActive(incoming.getIsActive());

            if (incoming.getEquipe() != null && incoming.getEquipe().getId() != null) {
                Equipe e = new Equipe();
                e.setId(incoming.getEquipe().getId());
                detail.setEquipe(e);
            }

            if (incoming.getUser() != null && incoming.getUser().getId() != null) {
                User u = new User();
                u.setId(incoming.getUser().getId());
                detail.setUser(u);
            }

            detailEquipeRepository.save(detail);
        }
    }

    // ================= MISSION UNITE (6) =================
    if (data.getMissionUnites() != null) {

        for (MissionUnite incoming : data.getMissionUnites()) {

            MissionUnite mu = new MissionUnite();

            mu.setId(incoming.getId());
            mu.setIsActive(incoming.getIsActive());

            if (incoming.getMission() != null && incoming.getMission().getId() != null) {
                Mission m = new Mission();
                m.setId(incoming.getMission().getId());
                mu.setMission(m);
            }

            if (incoming.getUnite() != null && incoming.getUnite().getId() != null) {
                Unite u = new Unite();
                u.setId(incoming.getUnite().getId());
                mu.setUnite(u);
            }

            missionUniteRepository.save(mu);
        }
    }

    // ================= EQUIPE UNITE (7) =================
    if (data.getEquipeUnites() != null) {

        for (EquipeUnite incoming : data.getEquipeUnites()) {

            EquipeUnite eu = new EquipeUnite();

            eu.setId(incoming.getId());
            eu.setIsActive(incoming.getIsActive());

            if (incoming.getEquipe() != null && incoming.getEquipe().getId() != null) {
                Equipe e = new Equipe();
                e.setId(incoming.getEquipe().getId());
                eu.setEquipe(e);
            }

            if (incoming.getUnite() != null && incoming.getUnite().getId() != null) {
                Unite u = new Unite();
                u.setId(incoming.getUnite().getId());
                eu.setUnite(u);
            }

            equipeUniteRepository.save(eu);
        }
    }

    // ================= DETAIL UNITE (8) =================
    if (data.getDetailUnites() != null) {

        for (DetailUnite du : data.getDetailUnites()) {

            DetailUnite entity;

            if (du.getId() != null) {
                entity = detailUniteRepository.findById(du.getId())
                        .orElse(new DetailUnite());
            } else {
                entity = new DetailUnite();
            }

            entity.setUser(
                    du.getUser() != null ? userRepository.findById(du.getUser().getId()).orElse(null) : null);

            entity.setUnite(
                    du.getUnite() != null ? uniteRepository.findById(du.getUnite().getId()).orElse(null) : null);

            entity.setIsActive(du.getIsActive());

            detailUniteRepository.save(entity);
        }
    }

    System.out.println("SYNC OK");
}
}