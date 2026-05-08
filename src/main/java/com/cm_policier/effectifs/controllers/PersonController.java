package com.cm_policier.effectifs.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.PersonRequest;
import com.cm_policier.effectifs.model.Person;
import com.cm_policier.effectifs.service.PersonService;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = "*")
public class PersonController {

    @Autowired
    private PersonService personService;

    // =========================
    // CREATE
    // =========================
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody PersonRequest request) {

        try {

            Person person = personService.create(request);

            return ResponseEntity.ok(Map.of(
                    "message", "Person created successfully",
                    "data", person
            ));

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Creation failed",
                    "error", e.getMessage()
            ));
        }
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping
    public ResponseEntity<List<Person>> getAll() {
        return ResponseEntity.ok(
                personService.getAll()
        );
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{uuid}")
    public ResponseEntity<?> getById(
            @PathVariable String uuid) {

        try {

            return ResponseEntity.ok(
                    personService.getById(uuid)
            );

        } catch (Exception e) {

            return ResponseEntity.status(404).body(Map.of(
                    "message", "Person not found",
                    "error", e.getMessage()
            ));
        }
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(
            @PathVariable String uuid,
            @RequestBody PersonRequest request) {

        try {

            Person updated = personService.update(uuid, request);

            return ResponseEntity.ok(Map.of(
                    "message", "Person updated successfully",
                    "data", updated
            ));

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Update failed",
                    "error", e.getMessage()
            ));
        }
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> delete(
            @PathVariable String uuid) {

        try {

            personService.delete(uuid);

            return ResponseEntity.ok(Map.of(
                    "message", "Person deleted successfully"
            ));

        } catch (Exception e) {

            return ResponseEntity.status(404).body(Map.of(
                    "message", "Delete failed",
                    "error", e.getMessage()
            ));
        }
    }
}