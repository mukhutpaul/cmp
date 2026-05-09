package com.cm_policier.effectifs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.CreateUniteRequest;
import com.cm_policier.effectifs.model.Person;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.repository.DetailUniteRepository;
import com.cm_policier.effectifs.repository.EquipeUniteRepository;
import com.cm_policier.effectifs.repository.MissionUniteRepository;
import com.cm_policier.effectifs.repository.PersonRepository;
import com.cm_policier.effectifs.repository.UniteRepository;

import jakarta.transaction.Transactional;

@Service
public class UniteService {

    @Autowired
    private UniteRepository uniteRepository;

    @Autowired
    private PersonRepository personRepository;

    // =========================
    // CREATE
    // =========================
    @Transactional
    public Unite create(CreateUniteRequest request) {

        Person commandant = null;

        if (request.getCommandantId() != null) {

            commandant = personRepository
                    .findById(request.getCommandantId())
                    .orElseThrow(() -> new RuntimeException("Commandant introuvable"));
        }

        Unite unite = Unite.builder()
                .name(request.getName())
                .signature(request.getSignature())
                .commandant(commandant)
                .build();

        return uniteRepository.save(unite);
    }

    // =========================
    // GET ALL
    // =========================
    public List<Unite> getAll() {
        return uniteRepository.findAll();
    }

    // =========================
    // GET BY ID
    // =========================
    public Unite getById(Long id) {

        return uniteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unité introuvable"));
    }

    // =========================
    // UPDATE
    // =========================
    @Transactional
    public Unite update(Long id, CreateUniteRequest request) {

        Unite existing = uniteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unité introuvable"));

        if (request.getName() != null) {
            existing.setName(request.getName());
        }

        if (request.getSignature() != null) {
            existing.setSignature(request.getSignature());
        }

        if (request.getCommandantId() != null) {

            Person commandant = personRepository
                    .findById(request.getCommandantId())
                    .orElseThrow(() -> new RuntimeException("Commandant introuvable"));

            existing.setCommandant(commandant);
        }

        return uniteRepository.save(existing);
    }

    // =========================
    // DELETE
    // =========================
    public void delete(Long id) {

        Unite unite = uniteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unité introuvable"));

        uniteRepository.delete(unite);
    }

    @Autowired
    private DetailUniteRepository detailRepo;

    @Autowired
    private MissionUniteRepository missionRepo;
    
    @Autowired
    private EquipeUniteRepository equipeRepo;

    public boolean verifierUnite(Long uniteId) {

        return detailRepo.existsByUnite_IdAndIsActiveTrue(uniteId)
                || missionRepo.existsByUnite_IdAndIsActiveTrue(uniteId)
                || equipeRepo.existsByUnite_IdAndIsActiveTrue(uniteId);
    }
}