package com.cm_policier.effectifs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.DetailEquipe;


public interface DetailEquipeRepository extends JpaRepository<DetailEquipe, Long> {
    List<DetailEquipe> findByEquipe_Id(Long equipeId);
    Optional<DetailEquipe> findByUser_Id(Long equipeId);
    
}