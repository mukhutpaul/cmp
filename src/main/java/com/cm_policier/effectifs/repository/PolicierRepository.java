package com.cm_policier.effectifs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cm_policier.effectifs.model.Policier;

@Repository
public interface PolicierRepository extends JpaRepository<Policier, Long> {
    Optional<Policier> findByMatricule(String matricule);
}
