package com.cm_policier.effectifs.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.MissionUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.repository.MissionUniteRepository;
import com.cm_policier.effectifs.repository.UniteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MissionUniteService {

    private final MissionUniteRepository repository;
    private final MissionRepository missionRepository;
    private final UniteRepository uniteRepository;

    

    // CREATE
    public MissionUnite create(Long missionId, Long uniteId) {

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission introuvable"));

        Unite unite = uniteRepository.findById(uniteId)
                .orElseThrow(() -> new RuntimeException("Unité introuvable"));

        MissionUnite mu = MissionUnite.builder()
                .mission(mission)
                .unite(unite)
                .isActive(true)
                .build();

        return repository.save(mu);
    }

    // GET ALL
    public List<MissionUnite> getAll() {
        return repository.findAll();
    }

    // GET BY ID
    public MissionUnite getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MissionUnite introuvable"));
    }

    // UPDATE
    public MissionUnite update(Long id, Long missionId, Long uniteId) {

        MissionUnite existing = getById(id);

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission introuvable"));

        Unite unite = uniteRepository.findById(uniteId)
                .orElseThrow(() -> new RuntimeException("Unité introuvable"));

        existing.setMission(mission);
        existing.setUnite(unite);

        return repository.save(existing);
    }

    // DELETE (soft delete)
    public void delete(Long id) {
        MissionUnite mu = getById(id);
        mu.setIsActive(false);
        repository.save(mu);
    }

    public List<Unite> getUnitesByMission(Long missionId) {

    List<MissionUnite> relations =
            repository.findByMissionId(missionId);

    return relations.stream()
            .map(MissionUnite::getUnite)
            .toList();
}

  public List<MissionUnite> findByMission(Long missionId) {
        return repository.findByMissionId(missionId);
    }
  
}