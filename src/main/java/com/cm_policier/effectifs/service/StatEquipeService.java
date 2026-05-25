package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.StatEquipeDto;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.repository.EquipeUniteRepository;
import com.cm_policier.effectifs.repository.PolicierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatEquipeService {

        private final EquipeRepository equipeRepository;
        private final EquipeUniteRepository equipeUniteRepository;
        private final PolicierRepository policierRepository;
        private final ControleRepository controleRepository;

        public List<StatEquipeDto> getStats() {

                List<Equipe> equipes = equipeRepository.findAll();

                return equipes.stream().map(equipe -> {

                        // unités équipe
                        List<String> unites = equipeUniteRepository
                                        .findByEquipeId(
                                                        equipe.getId())
                                        .stream()
                                        .map(eu -> eu.getUnite().getName())
                                        .toList();
                        long totalUnites = equipeUniteRepository
                                        .findByEquipeId(equipe.getId())
                                        .stream()
                                        .map(mu -> mu.getUnite().getName())
                                        .distinct()
                                        .count();

                        // total policiers
                        Long totalPoliciers = policierRepository.countByUnits(unites);

                        // total contrôles
                        Long totalControles = controleRepository.countByEquipeId(
                                        equipe.getId());

                        // présents
                        Long presents = controleRepository
                                        .countByEquipeIdAndPresentTrue(
                                                        equipe.getId());

                        // justifiés
                        Long justifies = controleRepository
                                        .countByEquipeIdAndJustifieTrue(
                                                        equipe.getId());

                        // non justifiés
                        Long nonJustifies = controleRepository
                                        .countByEquipeIdAndPresentFalseAndJustifieFalse(
                                                        equipe.getId());

                        return new StatEquipeDto(
                                        equipe.getId(),
                                        equipe.getUser().getUsername(),
                                        equipe.getMission().getNumero(),
                                        equipe.getMission().getZone(),
                                        totalPoliciers,
                                        totalControles,
                                        presents,
                                        justifies,
                                        nonJustifies,
                                        totalUnites);

                }).toList();
        }
}