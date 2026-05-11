package com.cm_policier.effectifs.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Session;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    // 🔵 session active globale
    Optional<Session> findByIsActiveTrue();

    // 🔵 session active par contrôleur (relation ManyToOne => controleur.id)
    Optional<Session> findByControleur_IdAndIsActiveTrue(Long controleurId);

    // 🔵 session active par séance
    Optional<Session> findBySeance_IdAndIsActiveTrue(UUID seanceId);
}