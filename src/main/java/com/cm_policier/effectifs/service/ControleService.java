package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.repository.ControleRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ControleService {

    private final ControleRepository repository;

    /* ========================= CREATE ========================= */
    public Controle create(Controle controle) {
        controle.setCreatedAt(java.time.LocalDateTime.now());
        return repository.save(controle);
    }

    /* ========================= READ ALL (PAGINATION) ========================= */
    public Page<Controle> getAll(int page, int size, String search) {

        PageRequest pageable = PageRequest.of(
                page,
                size,
                org.springframework.data.domain.Sort.by("createdAt").descending());

        if (search == null || search.isBlank()) {
            return repository.findAll(pageable);
        }

        return repository.search(search, pageable);
    }

    /* ========================= READ ONE ========================= */
    public Controle getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Controle introuvable"));
    }

    /* ========================= UPDATE ========================= */
    public Controle update(UUID id, Controle data) {
        Controle c = getById(id);

        c.setUid(data.getUid());
        c.setPolicier(data.getPolicier());
        c.setPresent(data.getPresent());
        c.setJustifie(data.getJustifie());
        c.setMatricule(data.getMatricule());
        c.setUnite(data.getUnite());
        c.setGrade(data.getGrade());
        c.setIsActif(data.getIsActif());

        return repository.save(c);
    }

    /* ========================= DELETE ========================= */
    public void delete(UUID id) {
        repository.deleteById(id);
    }

public List<Controle> searchByIdentite(
        String nom,
        String postnom,
        String prenom,
        LocalDate dateNaissance
) {
    return repository.searchByPolicierIdentite(
            nom,
            postnom,
            prenom,
            dateNaissance
    );
}
}
