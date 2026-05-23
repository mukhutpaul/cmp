package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.ControleResponseDto;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.service.ControleService;
import com.cm_policier.effectifs.service.DocumentService;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final DocumentService documentService;

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
    public List<Controle> searchByIdentite(
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

}