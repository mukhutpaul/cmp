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
                        LOWER(p.lastname) LIKE LOWER(CONCAT('%', :lastname, '%'))
                        AND LOWER(p.postname) LIKE LOWER(CONCAT('%', :postname, '%'))
                        AND LOWER(p.firstnames) LIKE LOWER(CONCAT('%', :firstname, '%'))
                        AND p.birthDate = :birthDate
                        """)
        List<Controle> searchByPolicierIdentite(
                        @Param("lastname") String lastname,
                        @Param("postname") String postname,
                        @Param("firstname") String firstname,
                        @Param("birthDate") LocalDate birthDate);

        long count();

        long countByPresentTrue();

        long countByJustifieTrue();

        long countByJustifieFalse();

        long countByPresentFalseAndJustifieFalse();

        List<Controle> findAllByOrderByUpdatedAtDesc();

        Optional<Controle> findByMatricule(String matricule);
}