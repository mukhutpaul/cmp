package com.cm_policier.effectifs.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.PolicierRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicierService {

    private final PolicierRepository repository;

    // CREATE
    public Policier create(Policier policier) {

        if (repository.existsByMatricule(policier.getMatricule())) {
            throw new RuntimeException("Matricule déjà utilisé");
        }

        return repository.save(policier);
    }

    public Page<Policier> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    // READ ONE
    public Policier findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policier introuvable"));
    }

    // UPDATE
    public Policier update(UUID id, Policier policier) {

        Policier existing = findById(id);

        existing.setMatricule(policier.getMatricule());
        existing.setNom(policier.getNom());
        existing.setPostnom(policier.getPostnom());
        existing.setPrenom(policier.getPrenom());
        existing.setSexe(policier.getSexe());
        existing.setDateNaissance(policier.getDateNaissance());
        existing.setLieuNaissance(policier.getLieuNaissance());
        existing.setTelephone(policier.getTelephone());
        existing.setEmail(policier.getEmail());
        existing.setAdresse(policier.getAdresse());
        existing.setCommune(policier.getCommune());
        existing.setStatut(policier.getStatut());

        // ajoute les autres champs si nécessaire

        return repository.save(existing);
    }

    // DELETE
    public void delete(UUID id) {

        Policier policier = findById(id);

        repository.delete(policier);
    }
}