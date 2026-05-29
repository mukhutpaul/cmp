package com.cm_policier.effectifs.service;

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

        RemoteSyncClient.sendToCentral(payload);
    }

    private SyncPayload buildPayload(
            List<Seance> seances,
            List<Session> sessions,
            List<Controle> controles,
            List<DocumentSyncDTO> documents
    ) {
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
            payload.getSessions().forEach(session -> {
                sessionRepository.save(session);
            });
        }

        // =========================
        // 3. CONTROLES (UPSERT + FLAGS)
        // =========================
        if (payload.getControles() != null) {
            payload.getControles().forEach(controle -> {

                controle.setIsSync(true);

                controleRepository.findById(controle.getId())
                        .map(existing -> {
                            existing.setPresent(controle.getPresent());
                            existing.setJustifie(controle.getJustifie());
                            existing.setVersionSync(
                                    Math.max(existing.getVersionSync(), controle.getVersionSync())
                            );
                            existing.setIsSync(true);
                            return controleRepository.save(existing);
                        })
                        .orElseGet(() -> controleRepository.save(controle));
            });
        }

        // =========================
        // 4. DOCUMENTS + IMAGES
        // =========================
        if (payload.getDocuments() != null) {
            payload.getDocuments().forEach(doc -> {

                try {
                    // decode image base64
                    byte[] imageBytes = java.util.Base64.getDecoder()
                            .decode(doc.getImageBase64());

                    String fileName = doc.getId() + ".jpg";
                    java.nio.file.Path path = java.nio.file.Paths.get(
                            "C:/bdd/document/" + fileName
                    );

                    java.nio.file.Files.createDirectories(path.getParent());
                    java.nio.file.Files.write(path, imageBytes);

                    Document entity = new Document();
                    entity.setId(doc.getId());
                    entity.setTitle(doc.getTitle());
                    entity.setImageUrl(fileName);
                    entity.setControle(
                            controleRepository.findById(doc.getControleId()).orElse(null)
                    );

                    documentRepository.save(entity);

                } catch (Exception e) {
                    throw new RuntimeException("Erreur sync document", e);
                }
            });
        }

        System.out.println("✅ SYNC COMPLETED SUCCESSFULLY");
    }
}
