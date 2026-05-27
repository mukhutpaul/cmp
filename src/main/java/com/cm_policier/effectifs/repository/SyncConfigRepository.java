package com.cm_policier.effectifs.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.SyncConfig;

public interface SyncConfigRepository
        extends JpaRepository<SyncConfig, Long> {

    SyncConfig findTopByOrderByIdDesc();
}