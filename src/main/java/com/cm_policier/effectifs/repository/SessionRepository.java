package com.cm_policier.effectifs.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
