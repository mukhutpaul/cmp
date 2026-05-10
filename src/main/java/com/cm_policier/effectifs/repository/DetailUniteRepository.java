package com.cm_policier.effectifs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.DetailUnite;
import java.util.List;

public interface DetailUniteRepository extends JpaRepository<DetailUnite, Long> {

    List<DetailUnite> findByIsActiveTrue();

    boolean existsByUniteId(Long uniteId);

    boolean existsByUnite_IdAndIsActiveTrue(Long uniteId);

    List<DetailUnite> findByUserId(Long userId);
}