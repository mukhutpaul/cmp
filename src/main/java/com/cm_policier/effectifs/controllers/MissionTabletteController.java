package com.cm_policier.effectifs.controllers;
import com.cm_policier.effectifs.model.MissionTablette;
import com.cm_policier.effectifs.service.MissionTabletteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mission-tablettes")
@RequiredArgsConstructor
public class MissionTabletteController {

    private final MissionTabletteService service;

    @PostMapping
    public ResponseEntity<MissionTablette> create(@RequestBody MissionTablette m) {
        return ResponseEntity.ok(service.create(m));
    }

    @GetMapping
    public ResponseEntity<List<MissionTablette>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionTablette> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionTablette> update(
            @PathVariable Long id,
            @RequestBody MissionTablette m) {
        return ResponseEntity.ok(service.update(id, m));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}