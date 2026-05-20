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

                User user;

                if (incoming.getId() != null) {
                    user = userRepository.findById(incoming.getId())
                            .orElseGet(() -> {
                                User u = new User();
                                u.setId(incoming.getId());
                                return u;
                            });
                } else {
                    user = new User();
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
                        .orElseGet(() -> {
                            Mission m = new Mission();
                            m.setId(incoming.getId());
                            return m;
                        });
            } else {
                mission = new Mission();
            }

            mission.setDateDebut(incoming.getDateDebut());
            mission.setDateFin(incoming.getDateFin());
            mission.setZone(incoming.getZone());
            mission.setNumero(incoming.getNumero());
            mission.setIsActive(incoming.getIsActive());

            if (incoming.getChargeMission() != null
                    && incoming.getChargeMission().getId() != null) {

                mission.setChargeMission(
                        userRepository.findById(
                                incoming.getChargeMission().getId())
                                .orElse(null));
            }

            missionRepository.save(mission);
        }

        // ================= EQUIPE =================
        if (data.getEquipe() != null) {

            Equipe incoming = data.getEquipe();

            Equipe equipe;

            if (incoming.getId() != null) {
                equipe = equipeRepository.findById(incoming.getId())
                        .orElseGet(() -> {
                            Equipe e = new Equipe();
                            e.setId(incoming.getId());
                            return e;
                        });
            } else {
                equipe = new Equipe();
            }

            equipe.setIsActive(incoming.getIsActive());

            if (incoming.getMission() != null
                    && incoming.getMission().getId() != null) {

                equipe.setMission(
                        missionRepository.findById(
                                incoming.getMission().getId())
                                .orElse(null));
            }

            if (incoming.getUser() != null
                    && incoming.getUser().getId() != null) {

                equipe.setUser(
                        userRepository.findById(
                                incoming.getUser().getId())
                                .orElse(null));
            }

            equipeRepository.save(equipe);
        }

        // ================= DETAIL EQUIPE =================
        if (data.getDetailEquipes() != null) {

            for (DetailEquipe incoming : data.getDetailEquipes()) {

                DetailEquipe detail;

                if (incoming.getId() != null) {
                    detail = detailEquipeRepository.findById(incoming.getId())
                            .orElseGet(() -> {
                                DetailEquipe d = new DetailEquipe();
                                d.setId(incoming.getId());
                                return d;
                            });
                } else {
                    detail = new DetailEquipe();
                }

                detail.setIsActive(incoming.getIsActive());

                if (incoming.getEquipe() != null
                        && incoming.getEquipe().getId() != null) {

                    detail.setEquipe(
                            equipeRepository.findById(
                                    incoming.getEquipe().getId())
                                    .orElse(null));
                }

                if (incoming.getUser() != null
                        && incoming.getUser().getId() != null) {

                    detail.setUser(
                            userRepository.findById(
                                    incoming.getUser().getId())
                                    .orElse(null));
                }

                detailEquipeRepository.save(detail);
            }
        }

        // ================= UNITES =================
        if (data.getUnites() != null) {

            for (Unite incoming : data.getUnites()) {

                Unite unite;

                if (incoming.getId() != null) {
                    unite = uniteRepository.findById(incoming.getId())
                            .orElseGet(() -> {
                                Unite u = new Unite();
                                u.setId(incoming.getId());
                                return u;
                            });
                } else {
                    unite = new Unite();
                }

                unite.setName(incoming.getName());
                unite.setSignature(incoming.getSignature());
                unite.setEquipeaf(incoming.getEquipeaf());

                if (incoming.getCommandant() != null
                        && incoming.getCommandant().getIdPersonnel() != null) {

                    Person p = new Person();
                    p.setIdPersonnel(
                            incoming.getCommandant().getIdPersonnel());

                    unite.setCommandant(p);
                }

                uniteRepository.save(unite);
            }
        }

        // ================= EQUIPE UNITE =================
        if (data.getEquipeUnites() != null) {

            for (EquipeUnite incoming : data.getEquipeUnites()) {

                EquipeUnite eu;

                if (incoming.getId() != null) {
                    eu = equipeUniteRepository.findById(incoming.getId())
                            .orElseGet(() -> {
                                EquipeUnite e = new EquipeUnite();
                                e.setId(incoming.getId());
                                return e;
                            });
                } else {
                    eu = new EquipeUnite();
                }

                eu.setIsActive(incoming.getIsActive());

                if (incoming.getEquipe() != null
                        && incoming.getEquipe().getId() != null) {

                    eu.setEquipe(
                            equipeRepository.findById(
                                    incoming.getEquipe().getId())
                                    .orElse(null));
                }

                if (incoming.getUnite() != null
                        && incoming.getUnite().getId() != null) {

                    eu.setUnite(
                            uniteRepository.findById(
                                    incoming.getUnite().getId())
                                    .orElse(null));
                }

                equipeUniteRepository.save(eu);
            }
        }

        // ================= MISSION UNITE =================
           System.out.println("MISSIONS SIZE = " 
        + data.getMissionUnites().size());
        if (data.getMissionUnites() != null) {

            for (MissionUnite incoming : data.getMissionUnites()) {

                MissionUnite mu;

                if (incoming.getId() != null) {
                    mu = missionUniteRepository.findById(incoming.getId())
                            .orElseGet(() -> {
                                MissionUnite m = new MissionUnite();
                                m.setId(incoming.getId());
                                return m;
                            });
                } else {
                    mu = new MissionUnite();
                }

                mu.setIsActive(incoming.getIsActive());

                if (incoming.getMission() != null
                        && incoming.getMission().getId() != null) {

                    mu.setMission(
                            missionRepository.findById(
                                    incoming.getMission().getId())
                                    .orElse(null));
                }

                if (incoming.getUnite() != null
                        && incoming.getUnite().getId() != null) {

                    mu.setUnite(
                            uniteRepository.findById(
                                    incoming.getUnite().getId())
                                    .orElse(null));
                }

                missionUniteRepository.save(mu);
            }
        }

        // ================= DETAIL UNITE =================
        System.out.println("DETAIL UNITES SIZE = " 
        + data.getDetailUnites().size());
        if (data.getDetailUnites() != null) {

            for (DetailUnite incoming : data.getDetailUnites()) {

                if (incoming.getId() == null) {
                    continue;
                }

                DetailUnite detail = detailUniteRepository
                        .findById(incoming.getId())
                        .orElse(new DetailUnite());

                detail.setId(incoming.getId());

                detail.setIsActive(incoming.getIsActive());

                // USER
                if (incoming.getUser() != null
                        && incoming.getUser().getId() != null) {

                    User user = userRepository
                            .findById(incoming.getUser().getId())
                            .orElse(null);

                    detail.setUser(user);
                }

                // UNITE
                if (incoming.getUnite() != null
                        && incoming.getUnite().getId() != null) {

                    Unite unite = uniteRepository
                            .findById(incoming.getUnite().getId())
                            .orElse(null);

                    detail.setUnite(unite);
                }

                System.out.println("SAVE DETAIL = " + detail.getId());

                detailUniteRepository.save(detail);
            }
        }

        System.out.println("SYNC OK CLEAN ✔");
    }
}

