package com.cm_policier.effectifs.repository;


import com.cm_policier.effectifs.model.Controle;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ControleRepository extends JpaRepository<Controle, UUID> {
}