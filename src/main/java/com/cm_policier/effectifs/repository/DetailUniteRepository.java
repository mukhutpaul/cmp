package com.cm_policier.effectifs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cm_policier.effectifs.model.DetailUnite;
import java.util.List;

public interface DetailUniteRepository extends JpaRepository<DetailUnite, Long> {

    List<DetailUnite> findByIsActiveTrue();

    boolean existsByUniteId(Long uniteId);

    boolean existsByUnite_IdAndIsActiveTrue(Long uniteId);

    List<DetailUnite> findByUser_Id(Long userId);

    List<DetailUnite> findByUnite_Id(Long uniteId);
    @Query("""
            SELECT DISTINCT du
            FROM DetailUnite du
            JOIN DetailEquipe de
            ON de.user.id = du.user.id 
            WHERE de.equipe.id = :equipeId
            """)
     List<DetailUnite> findByEquipe(@Param("equipeId") Long equipeId);
}