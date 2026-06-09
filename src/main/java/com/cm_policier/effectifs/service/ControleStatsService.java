package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.ControleStatsDto;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.repository.ControleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ControleStatsService {

        private final ControleRepository controleRepository;

        public ControleStatsDto getTodayStats() {

                LocalDate today = LocalDate.now();
                LocalDateTime start = today.atStartOfDay();
                LocalDateTime end = today.plusDays(1).atStartOfDay();

                List<Controle> controles = controleRepository.findByUpdatedAtToday(start, end);

                long totalControles = controles.size();

                long totalPresent = controles.stream()
                                .filter(c -> Boolean.TRUE.equals(c.getPresent()))
                                .count();

                long totalJustifie = controles.stream()
                                .filter(c -> Boolean.TRUE.equals(c.getJustifie()))
                                .count();

                long totalHommesPresent = controles.stream()
                                .filter(c -> "M".equalsIgnoreCase(c.getSexe()) && Boolean.TRUE.equals(c.getPresent()))
                                .count();

                long totalFemmesPresent = controles.stream()
                                .filter(c -> "F".equalsIgnoreCase(c.getSexe()) && Boolean.TRUE.equals(c.getPresent()))
                                .count();

                long totalHommesJustifies = controles.stream()
                                .filter(c -> "M".equalsIgnoreCase(c.getSexe()) && Boolean.TRUE.equals(c.getJustifie()))
                                .count();

                long totalFemmesJustifies = controles.stream()
                                .filter(c -> "F".equalsIgnoreCase(c.getSexe()) && Boolean.TRUE.equals(c.getJustifie()))
                                .count();

                long totalGlobal = controles.stream()
                                .filter(c -> Boolean.TRUE.equals(c.getPresent())
                                                || Boolean.TRUE.equals(c.getJustifie()))
                                .count();

                // 🔥 TOTAL PAR UNITE
                Map<String, Long> statsParUnite = controles.stream()
                                .filter(c -> c.getUnite() != null)
                                .collect(Collectors.groupingBy(
                                                Controle::getUnite,
                                                Collectors.counting()));

                // 🔥 PRESENT + JUSTIFIE PAR UNITE
                Map<String, Long> presentEtJustifieParUnite = controles.stream()
                                .filter(c -> c.getUnite() != null)
                                .filter(c -> Boolean.TRUE.equals(c.getPresent())
                                                || Boolean.TRUE.equals(c.getJustifie()))
                                .collect(Collectors.groupingBy(
                                                Controle::getUnite,
                                                Collectors.counting()));

                // 🔥 RESTE A CONTROLER PAR UNITE = TOTAL - (PRESENT + JUSTIFIE)
                Map<String, Long> resteParUnite = new java.util.HashMap<>();

                for (String unite : statsParUnite.keySet()) {

                        long total = statsParUnite.getOrDefault(unite, 0L);

                        long ok = presentEtJustifieParUnite.getOrDefault(unite, 0L);

                        resteParUnite.put(unite, total - ok);
                }

                return ControleStatsDto.builder()
                                .totalControles(totalControles)
                                .totalPresent(totalPresent)
                                .totalJustifie(totalJustifie)
                                .totalHommesPresent(totalHommesPresent)
                                .totalFemmesPresent(totalFemmesPresent)
                                .totalHommesJustifies(totalHommesJustifies)
                                .totalFemmesJustifies(totalFemmesJustifies)
                                .totalGlobalPresentEtJustifie(totalGlobal)
                                .totalUnites(statsParUnite.keySet().size())
                                .statsParUnite(resteParUnite)
                                .resteParUnite(resteParUnite) // 🔥 AJOUT ICI
                                .build();
        }

        public ControleStatsDto getStats() {

                List<Controle> controles = controleRepository.findAll();

                long totalControles = controles.size();

                long totalPresent = controles.stream()
                                .filter(c -> Boolean.TRUE.equals(c.getPresent()))
                                .count();

                long totalJustifie = controles.stream()
                                .filter(c -> Boolean.TRUE.equals(c.getJustifie()))
                                .count();

                long totalHommesPresent = controles.stream()
                                .filter(c -> "M".equalsIgnoreCase(c.getSexe())
                                                && Boolean.TRUE.equals(c.getPresent()))
                                .count();

                long totalFemmesPresent = controles.stream()
                                .filter(c -> "F".equalsIgnoreCase(c.getSexe())
                                                && Boolean.TRUE.equals(c.getPresent()))
                                .count();

                long totalHommesJustifies = controles.stream()
                                .filter(c -> "M".equalsIgnoreCase(c.getSexe())
                                                && Boolean.TRUE.equals(c.getJustifie()))
                                .count();

                long totalFemmesJustifies = controles.stream()
                                .filter(c -> "F".equalsIgnoreCase(c.getSexe())
                                                && Boolean.TRUE.equals(c.getJustifie()))
                                .count();

                long totalGlobal = controles.stream()
                                .filter(c -> Boolean.TRUE.equals(c.getPresent())
                                                || Boolean.TRUE.equals(c.getJustifie()))
                                .count();

                Map<String, Long> statsParUnite = controles.stream()
                                .filter(c -> c.getUnite() != null && !c.getUnite().isBlank())
                                .collect(Collectors.groupingBy(
                                                Controle::getUnite,
                                                Collectors.counting()));

                // 🔥 PRESENT + JUSTIFIE PAR UNITE
                Map<String, Long> presentEtJustifieParUnite = controles.stream()
                                .filter(c -> c.getUnite() != null)
                                .filter(c -> Boolean.TRUE.equals(c.getPresent())
                                                || Boolean.TRUE.equals(c.getJustifie()))
                                .collect(Collectors.groupingBy(
                                                Controle::getUnite,
                                                Collectors.counting()));

                // 🔥 RESTE A CONTROLER PAR UNITE = TOTAL - (PRESENT + JUSTIFIE)
                Map<String, Long> resteParUnite = new java.util.HashMap<>();

                for (String unite : statsParUnite.keySet()) {

                        long total = statsParUnite.getOrDefault(unite, 0L);

                        long ok = presentEtJustifieParUnite.getOrDefault(unite, 0L);

                        resteParUnite.put(unite, total - ok);
                }

                return ControleStatsDto.builder()
                                .totalControles(totalControles)
                                .totalPresent(totalPresent)
                                .totalJustifie(totalJustifie)
                                .totalHommesPresent(totalHommesPresent)
                                .totalFemmesPresent(totalFemmesPresent)
                                .totalHommesJustifies(totalHommesJustifies)
                                .totalFemmesJustifies(totalFemmesJustifies)
                                .totalGlobalPresentEtJustifie(totalGlobal)
                                .totalUnites(statsParUnite.size())
                                .statsParUnite(resteParUnite)
                                .build();
        }
}