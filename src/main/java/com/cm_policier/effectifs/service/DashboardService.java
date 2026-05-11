package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.DashboardStatsDTO;
import com.cm_policier.effectifs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PolicierRepository policierRepository;
    private final UniteRepository uniteRepository;
    private final EquipeRepository equipeRepository;
    private final MissionRepository missionRepository;
    private final ControleRepository controleRepository;

    public DashboardStatsDTO getStats() {

        long totalControles = controleRepository.count();
        long totalPresent = controleRepository.countByPresentTrue();
        long totalJustifies = controleRepository.countByJustifieTrue();
        long totalNonJustifies = controleRepository.countByJustifieFalse();

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