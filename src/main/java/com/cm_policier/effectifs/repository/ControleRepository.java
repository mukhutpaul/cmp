package com.cm_policier.effectifs.repository;

import com.cm_policier.effectifs.model.Controle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ControleRepository extends JpaRepository<Controle, UUID>, ControleRepositoryCustom {
        @Query("""
                            SELECT c FROM Controle c
                            LEFT JOIN c.policier p
                            WHERE (:search IS NULL OR
                                  LOWER(c.matricule) LIKE LOWER(CONCAT('%', :search, '%'))
                                  OR LOWER(c.uid) LIKE LOWER(CONCAT('%', :search, '%'))
                                  OR LOWER(c.noms) LIKE LOWER(CONCAT('%', :search, '%'))
                            )
                        """)
        Page<Controle> search(String search, Pageable pageable);

        @Query("""
                        SELECT c FROM Controle c
                        JOIN c.policier p
                        WHERE
                        LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))
                        AND LOWER(p.postnom) LIKE LOWER(CONCAT('%', :postnom, '%'))
                        AND LOWER(p.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))
                        AND p.dateNaissance = :dateNaissance
                        """)
        List<Controle> searchByPolicierIdentite(
                        @Param("nom") String nom,
                        @Param("postnom") String postnom,
                        @Param("prenom") String prenom,
                        @Param("dateNaissance") LocalDate dateNaissance);

        long count();

        long countByPresentTrue();

        long countByJustifieTrue();

        long countByJustifieFalse();

        long countByPresentFalseAndJustifieFalse();

        List<Controle> findAllByOrderByUpdatedAtDesc();

        Optional<Controle> findByMatricule(String matricule);
}