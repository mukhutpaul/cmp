package com.cm_policier.effectifs.repository;

import java.time.LocalDate;
import java.util.List;

import com.cm_policier.effectifs.model.Controle;

public interface ControleRepositoryCustom {
    List<Controle> searchByIdentite(
        String nom,
        String postnom,
        String prenom,
        LocalDate dateNaissance
    );
}