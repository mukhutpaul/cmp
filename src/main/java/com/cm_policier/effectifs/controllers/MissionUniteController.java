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

import com.cm_policier.effectifs.model.MissionUnite;
import com.cm_policier.effectifs.service.MissionUniteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mission-unites")
@RequiredArgsConstructor
public class MissionUniteController {

    private final MissionUniteService service;

    // CREATE
    @PostMapping
    public ResponseEntity<MissionUnite> create(
            @RequestParam Long missionId,
            @RequestParam Long uniteId
    ) {
        return ResponseEntity.ok(service.create(missionId, uniteId));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<MissionUnite>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<MissionUnite> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MissionUnite> update(
            @PathVariable Long id,
            @RequestParam Long missionId,
            @RequestParam Long uniteId
    ) {
        return ResponseEntity.ok(service.update(id, missionId, uniteId));
    }

    // DELETE (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted");
    }
}