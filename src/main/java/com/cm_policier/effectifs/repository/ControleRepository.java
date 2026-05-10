package com.cm_policier.effectifs.repository;


import com.cm_policier.effectifs.model.Controle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ControleRepository extends JpaRepository<Controle, Long> {
}