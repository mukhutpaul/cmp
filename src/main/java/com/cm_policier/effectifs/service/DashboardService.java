package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.DashboardStatsDTO;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.EquipeUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PolicierRepository policierRepository;
    private final UniteRepository uniteRepository;
    private final EquipeRepository equipeRepository;
    private final MissionRepository missionRepository;
    private final ControleRepository controleRepository;
    private final UserRepository userRepository;
    private final EquipeUniteRepository equipeUniteRepository;

    public DashboardStatsDTO getStats(String profile, Long userId) {

        // =====================================================
        // CHEF_EQUIPE
        // =====================================================
        if ("CHEF_EQUIPE".equalsIgnoreCase(profile)) {

            // 🔹 USER CONNECTÉ
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            // 🔹 ÉQUIPE DU USER
            Equipe equipe = equipeRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Équipe introuvable"));

            List<EquipeUnite> equipeUnites = equipeUniteRepository.findByEquipe(equipe);

            List<String> nomsUnites = equipeUnites.stream()
                    .map(eu -> eu.getUnite().getName())
                    .toList();

            long totalUnites = nomsUnites.size();

            long totalPoliciers = policierRepository.countByUnitIn(nomsUnites);


            // 🔹 CONTRÔLES LIÉS À L'ÉQUIPE
            long totalControles = controleRepository.countByEquipe(equipe);

            long totalPresent = controleRepository.countByEquipeAndPresentTrue(equipe);

            long totalJustifies = controleRepository.countByEquipeAndJustifieTrue(equipe);

            long totalNonJustifies = controleRepository
                    .countByEquipeAndPresentFalseAndJustifieFalse(equipe);

            // 🔹 MISSION
            long totalMissions = equipe.getMission() != null ? 1 : 0;

            return DashboardStatsDTO.builder()
                    .totalPoliciers(totalPoliciers)
                    .totalUnites(totalUnites)
                    .totalEquipes(1)
                    .totalMissions(totalMissions)

                    .totalControles(totalControles)
                    .totalPresent(totalPresent)
                    .totalJustifies(totalJustifies)
                    .totalNonJustifies(totalNonJustifies)
                    .build();
        }

        // =====================================================
        // ADMIN / GLOBAL
        // =====================================================

        long totalControles = controleRepository.count();

        long totalPresent = controleRepository.countByPresentTrue();

        long totalJustifies = controleRepository.countByJustifieTrue();

        long totalNonJustifies = controleRepository
                .countByPresentFalseAndJustifieFalse();

        return DashboardStatsDTO.builder()
                .totalPoliciers(policierRepository.count())
                .totalUnites(uniteRepository.count())
                .totalEquipes(equipeRepository.count())
                .totalMissions(missionRepository.count())

                .totalControles(totalControles)
                .totalPresent(totalPresent)
                .totalJustifies(totalJustifies)
                .totalNonJustifies(totalNonJustifies)
                .build();
    }
}