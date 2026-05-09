package com.cm_policier.effectifs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.MissionUnite;

public interface MissionUniteRepository extends JpaRepository<MissionUnite, Long> {
    boolean existsByMissionIdAndUniteId(Long missionId, Long uniteId);
      boolean existsByUnite_IdAndIsActiveTrue(Long uniteId);
}