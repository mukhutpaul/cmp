package com.cm_policier.effectifs.repository;


import com.cm_policier.effectifs.model.Controle;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ControleRepository extends JpaRepository<Controle, UUID> {
     @Query("""
        SELECT c FROM Controle c
        LEFT JOIN c.policier p
        WHERE (:search IS NULL OR
              LOWER(c.matricule) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(c.uid) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<Controle> search(String search, Pageable pageable);
}