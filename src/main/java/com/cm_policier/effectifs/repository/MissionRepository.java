package com.cm_policier.effectifs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Mission;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByIsActiveTrue();
}