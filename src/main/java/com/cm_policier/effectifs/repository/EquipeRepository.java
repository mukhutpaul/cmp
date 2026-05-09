package com.cm_policier.effectifs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.Equipe;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    
    
}