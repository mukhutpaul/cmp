package com.cm_policier.effectifs.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.SeanceRequest;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.service.SeanceService;

@RestController
@RequestMapping("/api/seances")
public class SeanceController {

    @Autowired
    private SeanceService seanceService;

    @PostMapping
    public ResponseEntity<Seance> create(@RequestBody SeanceRequest request) {
        return ResponseEntity.ok(seanceService.create(request));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Seance> start(@PathVariable UUID id) {
        return ResponseEntity.ok(seanceService.start(id));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<Seance> finish(@PathVariable UUID id) {
        return ResponseEntity.ok(seanceService.finish(id));
    }

    @GetMapping
    public ResponseEntity<List<Seance>> getAll() {
        return ResponseEntity.ok(seanceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seance> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(seanceService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seance> update(@PathVariable UUID id, @RequestBody Seance seance) {
        return ResponseEntity.ok(seanceService.update(id, seance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        seanceService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    // BONUS endpoints 👇

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<Seance>> getByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(seanceService.getByMission(missionId));
    }

    @GetMapping("/chef/{chefId}")
    public ResponseEntity<List<Seance>> getByChef(@PathVariable Long chefId) {
        return ResponseEntity.ok(seanceService.getByChef(chefId));
    }
}