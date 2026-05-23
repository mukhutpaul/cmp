package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.service.ControleService;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.service.DocumentService;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final ControleService controleService;
    @Autowired
    private DocumentService service;

    DocumentController(ControleService controleService) {
        this.controleService = controleService;
    }

    @PostMapping
    public ResponseEntity<Document> create(@RequestBody Document document) {
        return ResponseEntity.ok(service.create(document));
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> update(@PathVariable UUID id, @RequestBody Document document) {
        return ResponseEntity.ok(service.update(id, document));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

   
}
