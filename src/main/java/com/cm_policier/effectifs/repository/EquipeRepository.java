package com.cm_policier.effectifs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.Equipe;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Optional<Equipe> findByChefEquipe_Id(Long chefId);
    Optional<Equipe> findByMission_Id(Long missionId);
    
    
}