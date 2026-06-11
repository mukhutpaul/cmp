package com.cm_policier.effectifs.client;

import com.cm_policier.effectifs.dto.SyncBatchRequest;
import com.cm_policier.effectifs.dto.SyncBatchResponse;
import com.cm_policier.effectifs.dto.SyncPayload;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.DocumentRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.SessionRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;


@Service
@Slf4j
@AllArgsConstructor
public class RemoteSyncClient {
    private final ControleRepository controleRepository;
    private final SeanceRepository seanceRepository;
    private final SessionRepository sessionRepository;
    private final DocumentRepository documentRepository;

    

  
  public  void sendToCentral(SyncPayload payload) {

    try {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://10.101.153.164:8090/api/sync/import",
                        payload,
                        String.class
                );

        if (response.getStatusCode().is2xxSuccessful()) {

            markAsSynced(payload);

            System.out.println("✅ Synchronisation réussie");
        } else {

            System.err.println(
                    "❌ Echec synchronisation : "
                            + response.getStatusCode());
        }

    } catch (Exception e) {

        System.err.println("❌ Erreur connexion serveur central");

        e.printStackTrace();
    }
}

@Transactional
public void markAsSynced(SyncPayload payload) {

    // =========================
    // CONTROLES
    // =========================
    payload.getControles().forEach(controle -> {

        controleRepository.findById(controle.getId())
                .ifPresent(entity -> {

                    entity.setIsSync(true);

                    Integer version =
                            entity.getVersionSync() == null
                                    ? 1
                                    : entity.getVersionSync();

                    entity.setVersionSync(version + 1);

                    entity.setSyncedAt(LocalDateTime.now());

                    controleRepository.save(entity);
                });
    });

    // =========================
    // SEANCES
    // =========================
    payload.getSeances().forEach(seance -> {

        seanceRepository.findById(seance.getId())
                .ifPresent(entity -> {

                    entity.setIsSynchronized(true);
                    seanceRepository.save(entity);
                });
    });

    // =========================
    // SESSIONS
    // =========================
    payload.getSessions().forEach(session -> {

        sessionRepository.findById(session.getId())
                .ifPresent(entity -> {

                    // optionnel mais recommandé
                    entity.setIsSynchronized(true);

                    sessionRepository.save(entity);
                });
    });

    // =========================
    // DOCUMENTS
    // =========================
    payload.getDocuments().forEach(doc -> {

        documentRepository.findById(doc.getId())
                .ifPresent(entity -> {

                    entity.setIsSync(true);

                    documentRepository.save(entity);
                });
    });
}
}