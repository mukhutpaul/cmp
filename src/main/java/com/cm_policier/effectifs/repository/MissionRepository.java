package com.cm_policier.effectifs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Mission;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByIsActiveTrue();
    Optional<Mission> findByChargeMission_Id(Long userId);
    List<Mission> findAllByOrderByIdDesc();
    
}