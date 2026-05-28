package com.cm_policier.effectifs.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cm_policier.effectifs.model.Seance;

public interface SeanceRepository extends JpaRepository<Seance, UUID> {

    List<Seance> findByMissionId(Long missionId);

    List<Seance> findByChefEquipeId(Long chefId);

    Optional<Seance> findByIsActiveTrue();

    Optional<Seance> findFirstByIsActiveTrueAndDateFinIsNull();

    @Query("""
            SELECT COUNT(s)
            FROM Seance s
            WHERE s.isSynchronized = false 
            """)
    Long countUnsynchronized();
}