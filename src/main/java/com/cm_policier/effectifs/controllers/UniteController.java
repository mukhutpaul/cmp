package com.cm_policier.effectifs.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.CreateUniteRequest;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.service.UniteService;

@RestController
@RequestMapping("/api/unites")
@CrossOrigin(origins = "*")
public class UniteController {

    @Autowired
    private UniteService uniteService;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CreateUniteRequest request) {

        try {

            Unite unite = uniteService.create(request);

            return ResponseEntity.ok(Map.of(
                    "message", "Unité créée avec succès",
                    "data", unite));

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur création",
                    "error", e.getMessage()));
        }
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<List<Unite>> getAll() {

        return ResponseEntity.ok(
                uniteService.getAll());
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    uniteService.getById(id));

        } catch (Exception e) {

            return ResponseEntity.status(404).body(Map.of(
                    "message", "Unité introuvable",
                    "error", e.getMessage()));
        }
    }

    // =========================
    // UPDATE
    // =========================
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody CreateUniteRequest request) {

        try {

            Unite unite = uniteService.update(id, request);

            return ResponseEntity.ok(Map.of(
                    "message", "Unité modifiée avec succès",
                    "data", unite));

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erreur modification",
                    "error", e.getMessage()));
        }
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        try {

            uniteService.delete(id);

            return ResponseEntity.ok(Map.of(
                    "message", "Unité supprimée"));

        } catch (Exception e) {

            return ResponseEntity.status(404).body(Map.of(
                    "message", "Erreur suppression",
                    "error", e.getMessage()));
        }
    }
}