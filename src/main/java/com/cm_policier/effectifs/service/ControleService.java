package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.ControleResponseDto;
import com.cm_policier.effectifs.dto.DocumentResponseDto;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.util.*;
import com.cm_policier.effectifs.repository.ControleRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ControleService {

        private final DocumentService documentService;
        private final ControleRepository repository;

        /* ========================= CREATE ========================= */
        public Controle create(Controle controle) {
                controle.setCreatedAt(java.time.LocalDateTime.now());
                return repository.save(controle);
        }

        /* ========================= READ ALL (PAGINATION) ========================= */
        public Page<Controle> getAll(int page, int size, String search) {

                PageRequest pageable = PageRequest.of(
                                page,
                                size,
                                org.springframework.data.domain.Sort.by("createdAt").descending());

                if (search == null || search.isBlank()) {
                        return repository.findAll(pageable);
                }

                return repository.search(search, pageable);
        }

        /* ========================= READ ONE ========================= */
        public Controle getById(UUID id) {
                return repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Controle introuvable"));
        }

        public List<ControleResponseDto> getAll() {

                return repository.findAllByOrderByUpdatedAtDesc()
                                .stream()
                                .map(controle -> {

                                        List<Document> docs = controle.getDocuments();

                                        List<DocumentResponseDto> documents = docs.stream()
                                                        .map(doc -> DocumentResponseDto.builder()
                                                                        .id(doc.getId())
                                                                        .title(doc.getTitle())
                                                                        .description(doc.getDescription())
                                                                        .imageUrl(doc.getImageUrl())
                                                                        .build())
                                                        .collect(Collectors.toList());
                                        return ControleResponseDto.builder()
                                                        .id(controle.getId())
                                                        .uid(controle.getUid())
                                                        .policier(controle.getPolicier())
                                                        .controleur(controle.getControleur())
                                                        .chefEquipe(controle.getChefEquipe())
                                                        .chargeMission(controle.getChargeMission())
                                                        .seance(controle.getSeance())
                                                        .equipe(controle.getEquipe())
                                                        .mission(controle.getMission())
                                                        .justification(controle.getJustification())
                                                        .noms(controle.getNoms())
                                                        .present(controle.getPresent())
                                                        .justifie(controle.getJustifie())
                                                        .observation(controle.getObservation())
                                                        .isControle(controle.getIsControle())
                                                        .matricule(controle.getMatricule())
                                                        .unite(controle.getUnite())
                                                        .grade(controle.getGrade())
                                                        .sexe(controle.getSexe())
                                                        .fingerprint(controle.getFingerprint())
                                                        .fingerprint4(controle.getFingerprint4())
                                                        .isCmd(controle.getIsCmd())
                                                        .isActif(controle.getIsActif())
                                                        .isSync(controle.getIsSync())
                                                        .versionSync(controle.getVersionSync())
                                                        .qrcode(controle.getQrcode())
                                                        .province(controle.getProvince())
                                                        .deviceId(controle.getDeviceId())
                                                        .pkPhoto(controle.getPkPhoto())
                                                        .photoUrl(PhotoUtil.buildPhotoUrl(controle.getPkPhoto()))
                                                        .syncedAt(controle.getSyncedAt())
                                                        .createdAt(controle.getCreatedAt())
                                                        .updatedAt(controle.getUpdatedAt())
                                                        .documents(documents)
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }

        /* ========================= UPDATE ========================= */
        public Controle update(UUID id, Controle data) {
                Controle c = getById(id);
                c.setPresent(data.getPresent());
                c.setJustifie(data.getJustifie());

                return repository.save(c);
        }

        /* ========================= DELETE ========================= */
        public void delete(UUID id) {
                repository.deleteById(id);
        }

        public Controle findById(UUID id) {

                return repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Controle introuvable"));
        }

        public void findByMatricule(String matricule) {
                repository.findByMatricule(matricule);
        }

        public List<Controle> searchByIdentite(
                        String nom,
                        String postnom,
                        String prenom,
                        LocalDate dateNaissance) {
                return repository.searchByPolicierIdentite(
                                nom,
                                postnom,
                                prenom,
                                dateNaissance);
        }

        public Optional<Controle> getByMatricule(String matricule) {
                return repository.findByMatricule(matricule);
        }

        public List<Document> uploadDocuments(

                        UUID controleId,
                        String title,
                        String description,
                        List<MultipartFile> files

        ) {

                Controle controle = repository.findById(controleId)
                                .orElseThrow(() -> new RuntimeException("Controle introuvable"));

                List<Document> documents = new ArrayList<>();

                try {

                        // 🔥 création auto dossier
                        File folder = new File("C:/bdd/document/");

                        if (!folder.exists()) {
                                folder.mkdirs();
                        }

                        for (MultipartFile file : files) {

                                // nom unique
                                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                                Path path = Paths.get("C:/bdd/document/" + fileName);

                                Files.copy(
                                                file.getInputStream(),
                                                path,
                                                StandardCopyOption.REPLACE_EXISTING);

                                // URL accessible
                                String imageUrl = "http://localhost:8090/documents/" + fileName;

                                Document document = Document.builder()
                                                .controle(controle)
                                                .title(title)
                                                .description(description)
                                                .imageUrl(imageUrl)
                                                .build();

                                documents.add(
                                                documentService.create(document));
                        }

                        // 🔥 automatiquement justifié
                        controle.setJustifie(true);

                        repository.save(controle);

                        return documents;

                } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                }
        }

        public Controle markPresent(UUID id) {

                Controle controle = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Controle introuvable"));

                controle.setPresent(true);

                return repository.save(controle);
        }
}
