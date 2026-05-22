package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.ControleResponseDto;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.service.ControleService;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/controles")
@RequiredArgsConstructor
public class ControleController {

    private final ControleService service;

    /* ========================= CREATE ========================= */
    @PostMapping
    public Controle create(@RequestBody Controle controle) {
        return service.create(controle);
    }

    /* ========================= READ ALL (PAGINATION) ========================= */
    @GetMapping
    public List<ControleResponseDto> getAll() {
        return service.getAll();
    }

    /* ========================= READ ONE ========================= */
    @GetMapping("/{id}")
    public Controle getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<Controle> getByMatricule(@PathVariable String matricule) {

        Optional<Controle> controle = service.getByMatricule(matricule);

        return controle.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /* ========================= UPDATE ========================= */
    @PatchMapping("/{id}")
    public Controle update(@PathVariable UUID id, @RequestBody Controle controle) {
        return service.update(id, controle);
    }

    /* ========================= DELETE ========================= */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/search/identite")
    public List<Controle> searchByIdentite(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String postnom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance) {

        return service.searchByIdentite(
                nom,
                postnom,
                prenom,
                dateNaissance);
    }
}