package com.cm_policier.effectifs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.MissionTablette;
import java.util.List;

public interface MissionTabletteRepository extends JpaRepository<MissionTablette, Long> {

    List<MissionTablette> findByIsActiveTrue();
}
