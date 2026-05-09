package com.cm_policier.effectifs.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cm_policier.effectifs.model.EquipeUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.service.EquipeUniteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/equipe-unites")
@RequiredArgsConstructor
public class EquipeUniteController {

    private final EquipeUniteService service;

    // CREATE
    @PostMapping
    public ResponseEntity<EquipeUnite> create(
            @RequestParam Long equipeId,
            @RequestParam Long uniteId) {
        return ResponseEntity.ok(service.create(equipeId, uniteId));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<EquipeUnite>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}/unites")
    public ResponseEntity<List<Unite>> getUnitesByEquipe(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                service.getUnitesByEquipe(id));
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<EquipeUnite> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<EquipeUnite> update(
            @PathVariable Long id,
            @RequestParam Long equipeId,
            @RequestParam Long uniteId) {
        return ResponseEntity.ok(service.update(id, equipeId, uniteId));
    }

    // DELETE (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }
}
