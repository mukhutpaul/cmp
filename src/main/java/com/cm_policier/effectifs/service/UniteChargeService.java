package com.cm_policier.effectifs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cm_policier.effectifs.config.BusinessException;
import com.cm_policier.effectifs.dto.ChargerUniteRequest;
import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.EquipeUnite;
import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.MissionUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.DetailUniteRepository;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.repository.EquipeUniteRepository;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.repository.MissionUniteRepository;
import com.cm_policier.effectifs.repository.UniteRepository;
import com.cm_policier.effectifs.repository.UserRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UniteChargeService {

        private final DetailUniteRepository detailUniteRepository;
        private final MissionUniteRepository missionUniteRepository;
        private final EquipeUniteRepository equipeUniteRepository;

        private final UniteRepository uniteRepository;
        private final MissionRepository missionRepository;
        private final EquipeRepository equipeRepository;
        private final UserRepository userRepository;

        @Autowired
        private LogUserService logUserService;
        @Autowired
        private UserService userService;

        public void chargerUnite(ChargerUniteRequest req) {

                // ==============================
                // VALIDATION
                // ==============================

                if (req.uniteId() == null) {
                        throw new BusinessException("Unité obligatoire");
                }

                if (req.missionId() == null) {
                        throw new BusinessException("Mission obligatoire");
                }

                if (req.equipeId() == null) {
                        throw new BusinessException("Équipe obligatoire");
                }

                if (req.userId() == null) {
                        throw new BusinessException("Utilisateur obligatoire");
                }

                // ==============================
                // LOAD ENTITIES
                // ==============================

                Unite unite = uniteRepository.findById(req.uniteId())
                                .orElseThrow(() -> new BusinessException("Unité introuvable"));

                Mission mission = missionRepository.findById(req.missionId())
                                .orElseThrow(() -> new BusinessException("Mission introuvable"));

                Equipe equipe = equipeRepository.findById(req.equipeId())
                                .orElseThrow(() -> new BusinessException("Équipe introuvable"));

                // ⚠️ USER PAR ID
                User user = userRepository.findById(req.userId())
                                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

                // ==============================
                // ANTI DOUBLON
                // ==============================

                boolean uniteExisteDansDetail = detailUniteRepository.existsByUniteId(unite.getId());

                if (uniteExisteDansDetail) {
                        throw new BusinessException(
                                        "Cette unité est déjà chargée");
                }

                boolean missionExiste = missionUniteRepository
                                .existsByMissionIdAndUniteId(
                                                mission.getId(),
                                                unite.getId());

                if (missionExiste) {
                        throw new BusinessException(
                                        "Cette unité est déjà liée à cette mission");
                }

                boolean equipeExiste = equipeUniteRepository
                                .existsByEquipeIdAndUniteId(
                                                equipe.getId(),
                                                unite.getId());

                if (equipeExiste) {
                        throw new BusinessException(
                                        "Cette unité est déjà liée à cette équipe");
                }

                // ==============================
                // SAVE DETAIL UNITE
                // ==============================

                DetailUnite detailUnite = DetailUnite.builder()
                                .unite(unite)
                                .user(user)
                                .isActive(true)
                                .build();

                detailUniteRepository.save(detailUnite);

                // ==============================
                // SAVE MISSION UNITE
                // ==============================

                MissionUnite missionUnite = MissionUnite.builder()
                                .mission(mission)
                                .unite(unite)
                                .isActive(true)
                                .build();

                missionUniteRepository.save(missionUnite);

                // ==============================
                // SAVE EQUIPE UNITE
                // ==============================

                EquipeUnite equipeUnite = EquipeUnite.builder()
                                .equipe(equipe)
                                .unite(unite)
                                .isActive(true)
                                .build();

                equipeUniteRepository.save(equipeUnite);
                unite.setEquipeaf("Equipe-" + equipeUnite.getEquipe().getUser().getUsername() + " Ctr:"
                                + detailUnite.getUser().getUsername() + " Mission: "
                                + missionUnite.getMission().getZone());
                uniteRepository.save(unite);

                String username = CurrentUserUtil.getCurrentUsername();
                User users = userService.findByUsername(username);
                logUserService.saveLog(users, "Affectation de l'unité "+equipeUnite.getUnite().getName()+" à :" +
                                equipeUnite.getEquipe().getUser().getUsername() + "-" +
                                missionUnite.getMission().getZone() + " " + detailUnite.getUser().getUsername());

        }
}