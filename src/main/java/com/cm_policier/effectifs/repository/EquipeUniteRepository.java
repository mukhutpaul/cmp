package com.cm_policier.effectifs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.EquipeUnite;

public interface EquipeUniteRepository extends JpaRepository<EquipeUnite, Long> {
  boolean existsByEquipeIdAndUniteId(Long equipeId, Long uniteId);

  boolean existsByUnite_IdAndIsActiveTrue(Long uniteId);

  List<EquipeUnite> findByEquipeId(Long equipeId);
   List<EquipeUnite> findByEquipe(Equipe equipe);
   
}
