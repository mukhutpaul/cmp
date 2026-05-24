package com.cm_policier.effectifs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.User;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Optional<Equipe> findByUser_Id(Long chefId);
    Optional<Equipe> findByMission_Id(Long missionId);
    Optional<Equipe> findByUser(User user);

     Long countByMissionId(Long missionId);
    
    
}