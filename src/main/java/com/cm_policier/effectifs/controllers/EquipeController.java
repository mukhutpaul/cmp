package com.cm_policier.effectifs.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.service.EquipeService;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    @Autowired
    private EquipeService equipeService;

    @PostMapping
    public ResponseEntity<Equipe> create(@RequestBody Equipe equipe) {
        return ResponseEntity.ok(equipeService.create(equipe));
    }

    @GetMapping
    public ResponseEntity<List<Equipe>> getAll() {
        return ResponseEntity.ok(equipeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipe> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipeService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipe> update(@PathVariable Long id, @RequestBody Equipe equipe) {
        return ResponseEntity.ok(equipeService.update(id, equipe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        equipeService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}