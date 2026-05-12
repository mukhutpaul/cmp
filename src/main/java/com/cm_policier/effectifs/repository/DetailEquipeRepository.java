package com.cm_policier.effectifs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.DetailEquipe;
import com.cm_policier.effectifs.model.DetailUnite;

public interface DetailEquipeRepository extends JpaRepository<DetailEquipe, Long> {
    List<DetailUnite> findByEquipe_Id(Long equipeId);
    
}