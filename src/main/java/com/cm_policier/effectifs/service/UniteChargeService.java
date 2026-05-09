package com.cm_policier.effectifs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cm_policier.effectifs.config.BusinessException;
import com.cm_policier.effectifs.dto.ChargerUniteRequest;
import com.cm_policier.effectifs.dto.ChargerUniteResponse;
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

    public ChargerUniteResponse chargerUnite(ChargerUniteRequest req) {

        // ==============================
        // VALIDATION PROPRE
        // ==============================
        if (req.uniteId() == null)
            return new ChargerUniteResponse("ERROR", "Unité obligatoire");

        if (req.missionId() == null)
            return new ChargerUniteResponse("ERROR", "Mission obligatoire");

        if (req.equipeId() == null)
            return new ChargerUniteResponse("ERROR", "Équipe obligatoire");

        if (req.userId() == null)
            return new ChargerUniteResponse("ERROR", "Utilisateur obligatoire");

        // ==============================
        // LOAD ENTITIES
        // ==============================
        Unite unite = uniteRepository.findById(req.uniteId())
                .orElseThrow(() -> new BusinessException("Unité introuvable"));

        Mission mission = missionRepository.findById(req.missionId())
                .orElseThrow(() -> new BusinessException("Mission introuvable"));

        Equipe equipe = equipeRepository.findById(req.equipeId())
                .orElseThrow(() -> new BusinessException("Équipe introuvable"));

        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        // ==============================
        // CHECK STATUT UNITE
        // ==============================
        boolean dejaChargee = detailUniteRepository.existsByUniteId(unite.getId());
        boolean missionExiste = missionUniteRepository.existsByMissionIdAndUniteId(mission.getId(), unite.getId());
        boolean equipeExiste = equipeUniteRepository.existsByEquipeIdAndUniteId(equipe.getId(), unite.getId());

        if (dejaChargee || missionExiste || equipeExiste) {

            return new ChargerUniteResponse(
                    "DEJA_CHARGEE",
                    "Cette unité est déjà chargée ou affectée"
            );
        }

        // ==============================
        // SAVE DETAIL
        // ==============================
        DetailUnite detailUnite = DetailUnite.builder()
                .unite(unite)
                .user(user)
                .isActive(true)
                .build();

        detailUniteRepository.save(detailUnite);

        // ==============================
        // SAVE MISSION
        // ==============================
        MissionUnite missionUnite = MissionUnite.builder()
                .mission(mission)
                .unite(unite)
                .isActive(true)
                .build();

        missionUniteRepository.save(missionUnite);

        // ==============================
        // SAVE EQUIPE
        // ==============================
        EquipeUnite equipeUnite = EquipeUnite.builder()
                .equipe(equipe)
                .unite(unite)
                .isActive(true)
                .build();

        equipeUniteRepository.save(equipeUnite);

        // ==============================
        // SUCCESS RESPONSE
        // ==============================
        return new ChargerUniteResponse(
                "CHARGEE",
                "Unité chargée avec succès"
        );
    }
}