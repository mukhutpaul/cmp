package com.cm_policier.effectifs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.EquipeUnite;

public interface EquipeUniteRepository extends JpaRepository<EquipeUnite, Long> {
    boolean existsByEquipeIdAndUniteId(Long equipeId, Long uniteId);
      boolean existsByUnite_IdAndIsActiveTrue(Long uniteId);
}
