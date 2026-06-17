package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.config.buildPhotoUrl;
import com.cm_policier.effectifs.dto.ControleResponseDto;
import com.cm_policier.effectifs.dto.DocumentResponseDto;
import com.cm_policier.effectifs.dto.FaceRecognitionResponse;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.util.*;
import com.cm_policier.effectifs.repository.ControleRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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
        private final LogUserService logUserService;
        private final UserService userService;
        private final FaceRecognitionService faceRecognitionService;

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

        public List<ControleResponseDto> searchByIdentite(
                        String nom,
                        String postnom,
                        String prenom,
                        LocalDate dateNaissance) {

                List<Controle> controles = repository.searchByPolicierIdentite(
                                nom,
                                postnom,
                                prenom,
                                dateNaissance);

                return controles.stream().map(c -> {

                        ControleResponseDto dto = ControleResponseDto.builder()

                                        .id(c.getId())
                                        .uid(c.getUid())

                                        // RELATIONS
                                        .policier(c.getPolicier())
                                        .controleur(c.getControleur())
                                        .chefEquipe(c.getChefEquipe())
                                        .chargeMission(c.getChargeMission())
                                        .seance(c.getSeance())
                                        .equipe(c.getEquipe())
                                        .mission(c.getMission())
                                        .justification(c.getJustification())

                                        // INFORMATIONS
                                        .noms(c.getNoms())
                                        .present(c.getPresent())
                                        .justifie(c.getJustifie())
                                        .observation(c.getObservation())
                                        .isControle(c.getIsControle())

                                        .matricule(c.getMatricule())
                                        .unite(c.getUnite())
                                        .grade(c.getGrade())
                                        .sexe(c.getSexe())

                                        // BIOMETRIE
                                        .fingerprint(c.getFingerprint())
                                        .fingerprint4(c.getFingerprint4())

                                        // FLAGS
                                        .isCmd(c.getIsCmd())
                                        .isActif(c.getIsActif())
                                        .isSync(c.getIsSync())
                                        .versionSync(c.getVersionSync())

                                        // FILE
                                        .qrcode(c.getQrcode())
                                        .province(c.getProvince())
                                        .deviceId(c.getDeviceId())
                                        .photoUrl(buildPhotoUrl.buildPhotoUrls(c.getPkPhoto()))
                                        // TIMESTAMPS
                                        .syncedAt(c.getSyncedAt())
                                        .createdAt(c.getCreatedAt())
                                        .updatedAt(c.getUpdatedAt())

                                        .build();

                        return dto;

                }).toList();
        }

        public ControleResponseDto getByMatricule(String matricule) {

                Controle c = repository.findByMatricule(matricule)
                                .orElseThrow(() -> new RuntimeException("Controle introuvable"));

                ControleResponseDto dto = ControleResponseDto.builder()

                                .id(c.getId())
                                .uid(c.getUid())

                                // RELATIONS
                                .policier(c.getPolicier())
                                .controleur(c.getControleur())
                                .chefEquipe(c.getChefEquipe())
                                .chargeMission(c.getChargeMission())
                                .seance(c.getSeance())
                                .equipe(c.getEquipe())
                                .mission(c.getMission())
                                .justification(c.getJustification())

                                // INFORMATIONS
                                .noms(c.getNoms())
                                .present(c.getPresent())
                                .justifie(c.getJustifie())
                                .observation(c.getObservation())
                                .isControle(c.getIsControle())

                                .matricule(c.getMatricule())
                                .unite(c.getUnite())
                                .grade(c.getGrade())
                                .sexe(c.getSexe())

                                // BIOMETRIE
                                .fingerprint(c.getFingerprint())
                                .fingerprint4(c.getFingerprint4())

                                // FLAGS
                                .isCmd(c.getIsCmd())
                                .isActif(c.getIsActif())
                                .isSync(c.getIsSync())
                                .versionSync(c.getVersionSync())

                                // FILE
                                .qrcode(c.getQrcode())
                                .province(c.getProvince())
                                .deviceId(c.getDeviceId())
                                .photoUrl(buildPhotoUrl.buildPhotoUrls(c.getPkPhoto()))

                                // TIMESTAMPS
                                .syncedAt(c.getSyncedAt())
                                .createdAt(c.getCreatedAt())
                                .updatedAt(c.getUpdatedAt())

                                .build();

                // PHOTO URL
                if (c.getPkPhoto() != null
                                && !c.getPkPhoto().isEmpty()) {

                        dto.setPhotoUrl(c.getPkPhoto() + ".jpg");
                }
                String username = CurrentUserUtil.getCurrentUsername();
                User user = userService.findByUsername(username);
                logUserService.saveLog(user, "Recherche Ctr par matricule:" + dto.getMatricule());

                return dto;
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
                        // File folder = new File("C:/bdd/document/");
                        File folder = new File("bdd/document/");

                        if (!folder.exists()) {
                                folder.mkdirs();
                        }

                        for (MultipartFile file : files) {

                                // nom unique
                                String fileName = controle.getUid() + "_" + UUID.randomUUID() + "_"
                                                + file.getOriginalFilename();

                                // Path path = Paths.get("C:/bdd/document/" + fileName);
                                Path path = Paths.get("/bdd/document/" + fileName);

                                Files.copy(
                                                file.getInputStream(),
                                                path,
                                                StandardCopyOption.REPLACE_EXISTING);

                                // URL accessible
                                // String imageUrl = "http://localhost:8090/documents/" + fileName;
                                String imageUrl = fileName;
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
                        controle.setPresent(false);
                        controle.setIsSync(false);

                        repository.save(controle);
                        String username = CurrentUserUtil.getCurrentUsername();
                        User user = userService.findByUsername(username);
                        logUserService.saveLog(user, "Justification du policier:" + controle.getNoms());

                        return documents;

                } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                }
        }

        public Controle markPresent(UUID id) {

                Controle controle = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Controle introuvable"));

                controle.setPresent(true);
                controle.setJustifie(false);
                controle.setIsSync(false);
                String username = CurrentUserUtil.getCurrentUsername();
                User user = userService.findByUsername(username);
                logUserService.saveLog(user, "Contôle du Policier:" + controle.getNoms());

                return repository.save(controle);
        }

        public List<Controle> collectControles(List<Seance> seances) {

                return repository.findAll().stream()
                                .filter(controle -> {

                                        Seance seance = controle.getSeance();

                                        if (seance == null) {
                                                return false;
                                        }

                                        // Séance active
                                        if (Boolean.TRUE.equals(seance.getIsActive())) {

                                                return Boolean.TRUE.equals(controle.getPresent())
                                                                && Boolean.FALSE.equals(controle.getIsSync());
                                        }

                                        // Séance fermée
                                        return Boolean.FALSE.equals(controle.getIsSync());

                                })
                                .toList();
        }

        public ControleResponseDto getByFace(MultipartFile image) throws Exception {

                // 1. appel API Python
                FaceRecognitionResponse result = faceRecognitionService.recognize(image);

                if (result == null || !Boolean.TRUE.equals(result.getSuccess())) {
                        throw new RuntimeException("Visage non reconnu");
                }

                String filename = result.getFilename();

                // 2. recherche en base via pkPhoto
                Controle c = repository.findByPkPhoto(filename)
                                .orElseThrow(() -> new RuntimeException(
                                                "Controle introuvable pour photo: " + filename));

                // 3. construction DTO (TON CODE inchangé)
                ControleResponseDto dto = ControleResponseDto.builder()

                                .id(c.getId())
                                .uid(c.getUid())

                                // RELATIONS
                                .policier(c.getPolicier())
                                .controleur(c.getControleur())
                                .chefEquipe(c.getChefEquipe())
                                .chargeMission(c.getChargeMission())
                                .seance(c.getSeance())
                                .equipe(c.getEquipe())
                                .mission(c.getMission())
                                .justification(c.getJustification())

                                // INFORMATIONS
                                .noms(c.getNoms())
                                .present(c.getPresent())
                                .justifie(c.getJustifie())
                                .observation(c.getObservation())
                                .isControle(c.getIsControle())

                                .matricule(c.getMatricule())
                                .unite(c.getUnite())
                                .grade(c.getGrade())
                                .sexe(c.getSexe())

                                // BIOMETRIE
                                .fingerprint(c.getFingerprint())
                                .fingerprint4(c.getFingerprint4())

                                // FLAGS
                                .isCmd(c.getIsCmd())
                                .isActif(c.getIsActif())
                                .isSync(c.getIsSync())
                                .versionSync(c.getVersionSync())

                                // FILE
                                .qrcode(c.getQrcode())
                                .province(c.getProvince())
                                .deviceId(c.getDeviceId())
                                .photoUrl(buildPhotoUrl.buildPhotoUrls(c.getPkPhoto()))

                                // TIMESTAMPS
                                .syncedAt(c.getSyncedAt())
                                .createdAt(c.getCreatedAt())
                                .updatedAt(c.getUpdatedAt())

                                .build();

                // 4. override photoUrl (comme ton code)
                if (c.getPkPhoto() != null && !c.getPkPhoto().isEmpty()) {
                        dto.setPhotoUrl(c.getPkPhoto() + ".jpg");
                }

                // 5. LOGGING (inchangé)
                String username = CurrentUserUtil.getCurrentUsername();
                User user = userService.findByUsername(username);

                logUserService.saveLog(
                                user,
                                "Reconnaissance faciale controle matricule: " + dto.getMatricule());

                return dto;
        }
}
