package com.cm_policier.effectifs.repository;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Seance;

public interface SeanceRepository extends JpaRepository<Seance, Long> {

    List<Seance> findByMissionId(Long missionId);
    List<Seance> findByChefEquipeId(Long chefId);
    List<Seance> findByIsActiveTrue();
}