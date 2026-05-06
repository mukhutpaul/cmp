package com.cm_policier.effectifs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.DetailEquipe;

public interface DetailEquipeRepository extends JpaRepository<DetailEquipe, Long> {
}