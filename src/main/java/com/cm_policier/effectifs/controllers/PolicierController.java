package com.cm_policier.effectifs.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.PolicierDto;
import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.service.PolicierService;

import java.time.LocalDate;
import java.util.List;

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
    public List<PolicierDto> getAll() {
        return service.getPoliciers();
    }

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

    // SEARCH BY MATRICULE
    @GetMapping("/matricule/{matricule}")
    public PolicierDto getByMatricule(@PathVariable String matricule) {
        return service.findByMatricule(matricule);
    }

    // READ ONE
    @GetMapping("/{id}")
    public Policier getById(@PathVariable Long id) {
        return service.findById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Policier update(
            @PathVariable Long id,
            @RequestBody Policier policier) {

        return service.update(id, policier);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}