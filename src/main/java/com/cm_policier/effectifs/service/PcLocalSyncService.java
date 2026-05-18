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
            for (User u : data.getUsers()) {

                User user = userRepository.findById(u.getId())
                        .orElse(new User());

                user.setId(u.getId());
                user.setUsername(u.getUsername());
                user.setEmail(u.getEmail());
                user.setNoms(u.getNoms());
                user.setPassword(u.getPassword());
                user.setProfile(u.getProfile());

                userRepository.save(user);
            }
        }

        // ================= MISSION (IMPORTANT FIRST) =================
        Mission mission = null;

        if (data.getMission() != null) {

            Mission m = data.getMission();

            mission = missionRepository.findById(m.getId())
                    .orElse(new Mission());

            mission.setId(m.getId());
            mission.setDateDebut(m.getDateDebut());
            mission.setDateFin(m.getDateFin());
            mission.setZone(m.getZone());
            mission.setNumero(m.getNumero());
            mission.setIsActive(m.getIsActive());

            if (m.getChargeMission() != null && m.getChargeMission().getId() != null) {
                User ref = new User();
                ref.setId(m.getChargeMission().getId());
                mission.setChargeMission(ref);
            }

            mission = missionRepository.saveAndFlush(mission);
        }

        // ================= EQUIPE =================
        Equipe equipe = null;

        if (data.getEquipe() != null) {

            Equipe e = data.getEquipe();

            equipe = equipeRepository.findById(e.getId())
                    .orElse(new Equipe());

            equipe.setId(e.getId());
            equipe.setIsActive(e.getIsActive());

            // ⚠️ IMPORTANT: utiliser mission persistée
            equipe.setMission(mission != null ? mission : e.getMission());

            equipe.setUser(e.getUser());

            equipe = equipeRepository.saveAndFlush(equipe);
        }

        // ================= DETAIL EQUIPE =================
        if (data.getDetailEquipes() != null) {

            for (DetailEquipe d : data.getDetailEquipes()) {

                DetailEquipe entity = new DetailEquipe();

                entity.setId(d.getId());
                entity.setIsActive(d.getIsActive());

                if (equipe != null && d.getEquipe() != null) {
                    Equipe ref = new Equipe();
                    ref.setId(equipe.getId());
                    entity.setEquipe(ref);
                }

                if (d.getUser() != null) {
                    User ref = new User();
                    ref.setId(d.getUser().getId());
                    entity.setUser(ref);
                }

                detailEquipeRepository.save(entity);
            }
        }

        // ================= EQUIPE UNITE =================
        if (data.getEquipeUnites() != null) {

            for (EquipeUnite euIn : data.getEquipeUnites()) {

                EquipeUnite eu = new EquipeUnite();
                eu.setId(euIn.getId());
                eu.setIsActive(euIn.getIsActive());

                if (equipe != null) {
                    Equipe ref = new Equipe();
                    ref.setId(equipe.getId());
                    eu.setEquipe(ref);
                }

                if (euIn.getUnite() != null) {
                    Unite u = new Unite();
                    u.setId(euIn.getUnite().getId());
                    eu.setUnite(u);
                }

                equipeUniteRepository.save(eu);
            }
        }

        // ================= MISSION UNITE =================
        if (data.getMissionUnites() != null) {

            for (MissionUnite muIn : data.getMissionUnites()) {

                MissionUnite mu = new MissionUnite();
                mu.setId(muIn.getId());
                mu.setIsActive(muIn.getIsActive());

                if (mission != null) {
                    Mission ref = new Mission();
                    ref.setId(mission.getId());
                    mu.setMission(ref);
                }

                if (muIn.getUnite() != null) {
                    Unite u = new Unite();
                    u.setId(muIn.getUnite().getId());
                    mu.setUnite(u);
                }

                missionUniteRepository.save(mu);
            }
        }

        // ================= UNITES =================
        if (data.getUnites() != null) {

            for (Unite uIn : data.getUnites()) {

                Unite u = uniteRepository.findById(uIn.getId())
                        .orElse(new Unite());

                u.setId(uIn.getId());
                u.setName(uIn.getName());
                u.setSignature(uIn.getSignature());
                u.setEquipeaf(uIn.getEquipeaf());

                if (uIn.getCommandant() != null) {
                    Person p = new Person();
                    p.setIdPersonnel(uIn.getCommandant().getIdPersonnel());
                    u.setCommandant(p);
                }

                uniteRepository.save(u);
            }
        }

        // ================= DETAIL UNITE =================
        if (data.getDetailUnites() != null) {

            for (DetailUnite du : data.getDetailUnites()) {

                DetailUnite entity = detailUniteRepository.findById(du.getId())
                        .orElse(new DetailUnite());

                entity.setId(du.getId());
                entity.setIsActive(du.getIsActive());

                if (du.getUser() != null) {
                    User ref = new User();
                    ref.setId(du.getUser().getId());
                    entity.setUser(ref);
                }

                if (du.getUnite() != null) {
                    Unite ref = new Unite();
                    ref.setId(du.getUnite().getId());
                    entity.setUnite(ref);
                }

                detailUniteRepository.save(entity);
            }
        }

        System.out.println("SYNC OK COMPLET");
    }
}