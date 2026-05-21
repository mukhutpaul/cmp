package com.cm_policier.effectifs.service;

import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

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
    public Policier findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policier introuvable"));
    }

    // UPDATE
    public Policier update(Long id, Policier policier) {

        Policier existing = findById(id);

        existing.setMatricule(policier.getMatricule());
        existing.setLastname(policier.getLastname());
        existing.setPostname(policier.getPostname());
        existing.setFirstnames(policier.getFirstnames());
        existing.setGender(policier.getGender());
        existing.setBirthDate(policier.getBirthDate());
        existing.setLieu(policier.getLieu());
        existing.setTelephone(policier.getTelephone());


        // ajoute les autres champs si nécessaire

        return repository.save(existing);
    }

    // DELETE
    public void delete(Long id) {

        Policier policier = findById(id);

        repository.delete(policier);
    }

  
    public Policier findByMatricule(String matricule) {
        return repository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Policier introuvable"));
    }

    public Policier findByIdentite(
        String lastname,
        String postname,
        String firstnames,
        LocalDate birthDate
) {

    return repository
            .findByLastnameAndPostnameAndFirstnamesAndBirthDate(
                    lastname,
                    postname,
                    firstnames,
                    birthDate
            )
            .orElseThrow(() ->
                    new RuntimeException("Policier introuvable"));
}
}