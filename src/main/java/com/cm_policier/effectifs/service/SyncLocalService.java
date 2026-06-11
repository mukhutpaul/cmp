package com.cm_policier.effectifs.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.client.RemoteSyncClient;
import com.cm_policier.effectifs.dto.DocumentSyncDTO;
import com.cm_policier.effectifs.dto.SyncPayload;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.Session;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.DocumentRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.SessionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SyncLocalService {

    private final SeanceRepository seanceRepository;
    private final SessionRepository sessionRepository;
    private final ControleRepository controleRepository;
    private final DocumentRepository documentRepository;

    private final SeanceService seanceService;
    private final SessionService sessionService;
    private final ControleService controleService;
    private final DocumentService documentService;



    public void executeSync() {

        List<Seance> seances = seanceService.collectSeances();

        List<Session> sessions = sessionService.collectSessions(seances);

        List<Controle> controles = controleService.collectControles(seances);

        List<DocumentSyncDTO> documents = documentService.collectDocuments(controles);

        SyncPayload payload = buildPayload(seances, sessions, controles, documents);

        RemoteSyncClient e = new RemoteSyncClient(controleRepository, seanceRepository, sessionRepository,
                documentRepository);
        e.sendToCentral(payload);
    }

    private SyncPayload buildPayload(
            List<Seance> seances,
            List<Session> sessions,
            List<Controle> controles,
            List<DocumentSyncDTO> documents) {
        SyncPayload payload = new SyncPayload();
        payload.setSeances(seances);
        payload.setSessions(sessions);
        payload.setControles(controles);
        payload.setDocuments(documents);
        return payload;
    }

    public void process(SyncPayload payload) {

        System.out.println("🔄 START SYNC PROCESS");

        // =========================
        // 1. SEANCES (UPSERT)
        // =========================
        if (payload.getSeances() != null) {

            payload.getSeances().forEach(seance -> {

                seanceRepository.findById(seance.getId())
                        .map(existing -> {

                            existing.setDateSeance(seance.getDateSeance());
                            existing.setDateFin(seance.getDateFin());
                            existing.setIsActive(seance.getIsActive());

                            return seanceRepository.save(existing);

                        })
                        .orElseGet(() -> seanceRepository.save(seance));

            });
        }

        // =========================
        // 2. SESSIONS
        // =========================
        if (payload.getSessions() != null) {

            payload.getSessions().forEach(session -> sessionRepository.save(session));
        }

        // =========================
        // 3. CONTROLES (UPSERT)
        // =========================
        if (payload.getControles() != null) {

            payload.getControles().forEach(controle -> {

                System.out.println("=================================");
                System.out.println("Controle ID = " + controle.getId());

                if (controle.getDocuments() != null) {
                    System.out.println("Nb documents = " + controle.getDocuments().size());

                    controle.getDocuments().forEach(doc -> {
                        System.out.println("Document ID = " + doc.getId());
                    });
                } else {
                    System.out.println("Documents = null");
                }

                // ❗ IMPORTANT : empêcher Hibernate de tenter de merger les documents
                controle.setDocuments(new ArrayList<>());

                controle.setIsSync(true);

                controleRepository.findById(controle.getId())
                        .map(existing -> {

                            existing.setPresent(controle.getPresent());
                            existing.setJustifie(controle.getJustifie());

                            int existingVersion = existing.getVersionSync() == null
                                    ? 1
                                    : existing.getVersionSync();

                            int incomingVersion = controle.getVersionSync() == null
                                    ? 1
                                    : controle.getVersionSync();

                            existing.setVersionSync(
                                    Math.max(existingVersion, incomingVersion));

                            existing.setIsSync(true);
                            existing.setSyncedAt(java.time.LocalDateTime.now());

                            return controleRepository.save(existing);

                        })
                        .orElseGet(() -> {

                            if (controle.getVersionSync() == null) {
                                controle.setVersionSync(1);
                            }

                            controle.setIsSync(true);
                            controle.setSyncedAt(java.time.LocalDateTime.now());

                            return controleRepository.save(controle);
                        });
            });
        }
        // =========================
        // 4. DOCUMENTS + IMAGES
        // =========================
        if (payload.getDocuments() != null) {

            payload.getDocuments().forEach(doc -> {

                try {

                    // =========================
                    // 1. validation image
                    // =========================
                    if (doc.getImageBase64() == null || doc.getImageBase64().isBlank()) {
                        System.err.println("Document sans image : " + doc.getId());
                        return;
                    }

                    // =========================
                    // 2. decode image
                    // =========================
                    byte[] imageBytes = java.util.Base64.getDecoder()
                            .decode(doc.getImageBase64());

                    String fileName = doc.getId() + ".jpg";

                    java.nio.file.Path path = java.nio.file.Paths.get(
                            "C:/bdd/document/" + fileName);

                    java.nio.file.Files.createDirectories(path.getParent());

                    java.nio.file.Files.write(
                            path,
                            imageBytes,
                            java.nio.file.StandardOpenOption.CREATE,
                            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);

                    // =========================
                    // 3. UPSERT SAFE (IMPORTANT)
                    // =========================
                    Document entity = documentRepository.findById(doc.getId())
                            .orElseGet(Document::new);

                    entity.setId(doc.getId());
                    entity.setTitle(doc.getTitle());

                    // ⚠️ ON STOCKE JUSTE LE NOM DU FICHIER
                    entity.setImageUrl(fileName);

                    // =========================
                    // 4. controle (optionnel mais safe)
                    // =========================
                    if (doc.getControleId() != null) {
                        controleRepository.findById(doc.getControleId())
                                .ifPresent(entity::setControle);
                    }

                    documentRepository.save(entity);

                } catch (Exception e) {

                    System.err.println("Erreur document : " + doc.getId());
                    e.printStackTrace();
                }
            });
        }
        System.out.println("✅ SYNC COMPLETED SUCCESSFULLY");
    }
}
