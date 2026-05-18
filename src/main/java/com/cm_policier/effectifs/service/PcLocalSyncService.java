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

        // =====================================================
        // 1. USERS (toujours en premier)
        // =====================================================
        if (data.getUsers() != null) {
            for (User incoming : data.getUsers()) {

                User user = (incoming.getId() != null)
                        ? userRepository.findById(incoming.getId()).orElse(new User())
                        : new User();

                user.setId(incoming.getId());
                user.setUsername(incoming.getUsername());
                user.setEmail(incoming.getEmail());
                user.setNoms(incoming.getNoms());
                user.setPassword(incoming.getPassword());
                user.setProfile(incoming.getProfile());

                userRepository.save(user);
            }
        }

        // =====================================================
        // 2. MISSIONS (avant équipe)
        // =====================================================
        if (data.getMission() != null) {

            Mission incoming = data.getMission();

            Mission mission = (incoming.getId() != null)
                    ? missionRepository.findById(incoming.getId()).orElse(new Mission())
                    : new Mission();

            mission.setDateDebut(incoming.getDateDebut());
            mission.setDateFin(incoming.getDateFin());
            mission.setZone(incoming.getZone());
            mission.setNumero(incoming.getNumero());
            mission.setIsActive(incoming.getIsActive());

            // relation user propre
            if (incoming.getChargeMission() != null && incoming.getChargeMission().getId() != null) {
                mission.setChargeMission(
                        userRepository.findById(incoming.getChargeMission().getId()).orElse(null)
                );
            }

            missionRepository.save(mission);
        }

        // =====================================================
        // 3. EQUIPES (dépend de mission + user)
        // =====================================================
        if (data.getEquipe() != null) {

            Equipe incoming = data.getEquipe();

            Equipe equipe = (incoming.getId() != null)
                    ? equipeRepository.findById(incoming.getId()).orElse(new Equipe())
                    : new Equipe();

            equipe.setIsActive(incoming.getIsActive());

            if (incoming.getMission() != null && incoming.getMission().getId() != null) {
                equipe.setMission(
                        missionRepository.findById(incoming.getMission().getId()).orElse(null)
                );
            }

            if (incoming.getUser() != null && incoming.getUser().getId() != null) {
                equipe.setUser(
                        userRepository.findById(incoming.getUser().getId()).orElse(null)
                );
            }

            equipeRepository.save(equipe);
        }

        // =====================================================
        // 4. DETAIL EQUIPE
        // =====================================================
        if (data.getDetailEquipes() != null) {

            for (DetailEquipe incoming : data.getDetailEquipes()) {

                DetailEquipe entity = (incoming.getId() != null)
                        ? detailEquipeRepository.findById(incoming.getId()).orElse(new DetailEquipe())
                        : new DetailEquipe();

                entity.setIsActive(incoming.getIsActive());

                if (incoming.getEquipe() != null && incoming.getEquipe().getId() != null) {
                    entity.setEquipe(
                            equipeRepository.findById(incoming.getEquipe().getId()).orElse(null)
                    );
                }

                if (incoming.getUser() != null && incoming.getUser().getId() != null) {
                    entity.setUser(
                            userRepository.findById(incoming.getUser().getId()).orElse(null)
                    );
                }

                detailEquipeRepository.save(entity);
            }
        }

        // =====================================================
        // 5. UNITES
        // =====================================================
        if (data.getUnites() != null) {

            for (Unite incoming : data.getUnites()) {

                Unite unite = (incoming.getId() != null)
                        ? uniteRepository.findById(incoming.getId()).orElse(new Unite())
                        : new Unite();

                unite.setName(incoming.getName());
                unite.setSignature(incoming.getSignature());
                unite.setEquipeaf(incoming.getEquipeaf());

                if (incoming.getCommandant() != null && incoming.getCommandant().getIdPersonnel() != null) {
                    Person p = new Person();
                    p.setIdPersonnel(incoming.getCommandant().getIdPersonnel());
                    unite.setCommandant(p);
                }

                uniteRepository.save(unite);
            }
        }

        // =====================================================
        // 6. EQUIPE UNITE
        // =====================================================
        if (data.getEquipeUnites() != null) {

            for (EquipeUnite incoming : data.getEquipeUnites()) {

                EquipeUnite eu = new EquipeUnite();

                eu.setIsActive(incoming.getIsActive());

                if (incoming.getEquipe() != null && incoming.getEquipe().getId() != null) {
                    eu.setEquipe(
                            equipeRepository.findById(incoming.getEquipe().getId()).orElse(null)
                    );
                }

                if (incoming.getUnite() != null && incoming.getUnite().getId() != null) {
                    eu.setUnite(
                            uniteRepository.findById(incoming.getUnite().getId()).orElse(null)
                    );
                }

                equipeUniteRepository.save(eu);
            }
        }

        // =====================================================
        // 7. MISSION UNITE
        // =====================================================
        if (data.getMissionUnites() != null) {

            for (MissionUnite incoming : data.getMissionUnites()) {

                MissionUnite mu = new MissionUnite();

                mu.setIsActive(incoming.getIsActive());

                if (incoming.getMission() != null && incoming.getMission().getId() != null) {
                    mu.setMission(
                            missionRepository.findById(incoming.getMission().getId()).orElse(null)
                    );
                }

                if (incoming.getUnite() != null && incoming.getUnite().getId() != null) {
                    mu.setUnite(
                            uniteRepository.findById(incoming.getUnite().getId()).orElse(null)
                    );
                }

                missionUniteRepository.save(mu);
            }
        }

        // =====================================================
        // 8. DETAIL UNITE
        // =====================================================
        if (data.getDetailUnites() != null) {

            for (DetailUnite incoming : data.getDetailUnites()) {

                DetailUnite entity = (incoming.getId() != null)
                        ? detailUniteRepository.findById(incoming.getId()).orElse(new DetailUnite())
                        : new DetailUnite();

                entity.setIsActive(incoming.getIsActive());

                if (incoming.getUser() != null && incoming.getUser().getId() != null) {
                    entity.setUser(
                            userRepository.findById(incoming.getUser().getId()).orElse(null)
                    );
                }

                if (incoming.getUnite() != null && incoming.getUnite().getId() != null) {
                    entity.setUnite(
                            uniteRepository.findById(incoming.getUnite().getId()).orElse(null)
                    );
                }

                detailUniteRepository.save(entity);
            }
        }

        System.out.println("SYNC OK COMPLET");
    }
}