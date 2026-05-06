package com.cm_policier.effectifs.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.service.MissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Mission> create(@RequestBody Mission mission) {
        return ResponseEntity.ok(missionService.create(mission));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<List<Mission>> getAll() {
        return ResponseEntity.ok(missionService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Mission> getById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Mission> update(@PathVariable Long id,
                                          @RequestBody Mission mission) {
        return ResponseEntity.ok(missionService.update(id, mission));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        missionService.delete(id);
        return ResponseEntity.ok().build();
    }
}