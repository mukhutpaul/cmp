package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.ControleResponseDto;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.repository.DocumentRepository;
import com.cm_policier.effectifs.service.ControleService;
import com.cm_policier.effectifs.service.DocumentService;

import lombok.RequiredArgsConstructor;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/controles")
@RequiredArgsConstructor
public class ControleController {

    private final ControleService service;
    private final DocumentRepository documentRepository;

    /* ========================= CREATE ========================= */
    @PostMapping
    public Controle create(@RequestBody Controle controle) {
        return service.create(controle);
    }

    /* ========================= READ ALL (PAGINATION) ========================= */
    @GetMapping
    public List<ControleResponseDto> getAll() {
        return service.getAll();
    }

    /* ========================= READ ONE ========================= */
    @GetMapping("/{id}")
    public Controle getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<ControleResponseDto> getByMatricule(
            @PathVariable String matricule) {

        ControleResponseDto controle = service.getByMatricule(matricule);

        return ResponseEntity.ok(controle);
    }

    /* ========================= UPDATE ========================= */
    @PatchMapping("/{id}")
    public Controle update(@PathVariable UUID id, @RequestBody Controle controle) {
        return service.update(id, controle);
    }

    /* ========================= DELETE ========================= */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/search/identite")
    public List<ControleResponseDto> searchByIdentite(

            @RequestParam(required = false) String nom,

            @RequestParam(required = false) String postnom,

            @RequestParam(required = false) String prenom,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance) {

        return service.searchByIdentite(
                nom,
                postnom,
                prenom,
                dateNaissance);
    }

    @PostMapping(value = "/{controleId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocuments(

            @PathVariable UUID controleId,

            @RequestPart("title") String title,

            @RequestPart("description") String description,

            @RequestPart("files") List<MultipartFile> files

    ) {
        return ResponseEntity.ok(
                service.uploadDocuments(
                        controleId,
                        title,
                        description,
                        files));
    }

    @PatchMapping("/present")
    public ResponseEntity<?> markPresent(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(service.markPresent(id));
    }

    @PatchMapping("/{id}/invalidate")
    public ResponseEntity<?> invalidateControle(@PathVariable UUID id) {

        try {

            Controle controle = service.findById(id);

            if (controle == null) {
                return ResponseEntity.badRequest()
                        .body("Contrôle introuvable");
            }

            // =========================
            // RECUP DOCUMENTS
            // =========================

            List<Document> documents = documentRepository.findByControle(controle);

            // =========================
            // DELETE PHOTOS
            // =========================

            for (Document doc : documents) {

                try {

                    if (doc.getImageUrl() != null &&
                            !doc.getImageUrl().isBlank()) {

                        Path path = Paths.get(
                                "C:/bdd/document/",
                                doc.getImageUrl());

                        Files.deleteIfExists(path);
                    }

                } catch (Exception e) {

                    System.out.println(
                            "Erreur suppression fichier : "
                                    + e.getMessage());
                }
            }

            // =========================
            // DELETE DOCUMENTS DATABASE
            // =========================

            documentRepository.deleteAll(documents);

            // =========================
            // INVALIDATION
            // =========================

            controle.setPresent(false);
            controle.setJustifie(false);
            controle.setIsSync(false);

            controle.setObservation("Contrôle invalidé");

            controle.setUpdatedAt(LocalDateTime.now());

            service.create(controle);

            return ResponseEntity.ok(
                    "Contrôle invalidé avec succès");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }
}