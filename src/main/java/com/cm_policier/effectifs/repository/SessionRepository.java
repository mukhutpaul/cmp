package com.cm_policier.effectifs.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.Session;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findByUserIdAndActiveTrue(Long userId);

    Optional<Session> findBySeanceIdAndActiveTrue(Long seanceId);

    Optional<Session> findByControleurIdAndIsActiveTrue(Long controleurId);

    Optional<Session> findByIsActiveTrue();
}
