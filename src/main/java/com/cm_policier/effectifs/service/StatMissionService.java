package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.StatMissionDto;
import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.MissionUnite;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.repository.MissionUniteRepository;
import com.cm_policier.effectifs.repository.PolicierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatMissionService {

    private final MissionRepository missionRepository;
    private final MissionUniteRepository missionUniteRepository;
    private final PolicierRepository policierRepository;
    private final ControleRepository controleRepository;
    private final EquipeRepository equipeRepository;

    public List<StatMissionDto> getStats() {

        List<Mission> missions = missionRepository.findAll();

        return missions.stream().map(mission -> {

            // récupérer unités de la mission
            List<String> unites = missionUniteRepository
                    .findByMissionIdAndIsActiveTrue(mission.getId())
                    .stream()
                    .map(mu -> mu.getUnite().getName())
                    .toList();

            long totalUnites = missionUniteRepository
                    .findByMissionIdAndIsActiveTrue(mission.getId())
                    .stream()
                    .map(mu -> mu.getUnite().getName())
                    .distinct()
                    .count();

            // total policiers
            Long totalPoliciers = policierRepository.countByUnits(unites);

            // total contrôles
            Long totalControles = controleRepository.countByMissionId(mission.getId());

            // présents
            Long presents = controleRepository.countByMissionIdAndPresentTrue(mission.getId());

            // justifiés
            Long justifies = controleRepository.countByMissionIdAndJustifieTrue(mission.getId());

            // non justifiés
            Long nonJustifies = controleRepository.countByMissionIdAndPresentFalseAndJustifieFalse(mission.getId());

            // équipes
            Long totalEquipes = equipeRepository.countByMissionId(mission.getId());

            return new StatMissionDto(
                    mission.getId(),
                    mission.getNumero(),
                    mission.getZone(),
                    totalPoliciers,
                    totalControles,
                    presents,
                    justifies,
                    nonJustifies,
                    totalEquipes,
                    totalUnites // ✅ AJOUT ICI
            );

        }).toList();
    }
}