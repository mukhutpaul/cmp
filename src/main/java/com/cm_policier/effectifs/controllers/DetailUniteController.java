package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.service.DetailUniteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detail-unites")
@RequiredArgsConstructor
public class DetailUniteController {

    private final DetailUniteService service;

    @PostMapping
    public ResponseEntity<DetailUnite> create(@RequestBody DetailUnite d) {
        return ResponseEntity.ok(service.create(d));
    }

    @GetMapping
    public ResponseEntity<List<DetailUnite>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailUnite> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetailUnite> update(
            @PathVariable Long id,
            @RequestBody DetailUnite d) {
        return ResponseEntity.ok(service.update(id, d));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/unites")
    public ResponseEntity<List<Unite>> getUnitesByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                service.getUnitesByUser(userId));
    }
}
