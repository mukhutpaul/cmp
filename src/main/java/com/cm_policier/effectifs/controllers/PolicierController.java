package com.cm_policier.effectifs.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.service.PolicierService;
import java.time.LocalDate;

import java.util.UUID;

@RestController
@RequestMapping("/api/policiers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PolicierController {

    private final PolicierService service;

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Policier create(@RequestBody Policier policier) {
        return service.create(policier);
    }

    // READ ALL
    @GetMapping
    public Page<Policier> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.getAll(page, size);
    }

    // ========================= SEARCH BY MATRICULE =========================

    // SEARCH BY IDENTITE
    @GetMapping("/identite")
    public Policier getByIdentite(

            @RequestParam String nom,
            @RequestParam String postnom,
            @RequestParam String prenom,
            @RequestParam LocalDate dateNaissance

    ) {

        return service.findByIdentite(
                nom,
                postnom,
                prenom,
                dateNaissance);
    }

    @GetMapping("/matricule/{matricule}")
    public Policier getByMatricule(@PathVariable String matricule) {
        return service.findByMatricule(matricule);
    }

    // READ ONE
    @GetMapping("/{id}")
    public Policier getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Policier update(
            @PathVariable UUID id,
            @RequestBody Policier policier) {
        return service.update(id, policier);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}