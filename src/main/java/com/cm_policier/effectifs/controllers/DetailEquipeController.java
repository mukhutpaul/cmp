package com.cm_policier.effectifs.controllers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.model.DetailEquipe;
import com.cm_policier.effectifs.service.DetailEquipeService;

@RestController
@RequestMapping("/api/detail-equipes")
public class DetailEquipeController {

    @Autowired
    private DetailEquipeService service;

    @PostMapping
    public ResponseEntity<DetailEquipe> create(@RequestBody DetailEquipe detail) {
        return ResponseEntity.ok(service.create(detail));
    }

    @GetMapping
    public ResponseEntity<List<DetailEquipe>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailEquipe> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetailEquipe> update(@PathVariable Long id, @RequestBody DetailEquipe detail) {
        return ResponseEntity.ok(service.update(id, detail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}