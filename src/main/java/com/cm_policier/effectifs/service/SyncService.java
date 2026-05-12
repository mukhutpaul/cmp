package com.cm_policier.effectifs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.SyncPayload;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.Session;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.DocumentRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.SessionRepository;

@Service
public class SyncService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private ControleRepository controleRepository;

    @Autowired
    private DocumentRepository documentRepository;

    public SyncPayload buildSyncPayload() {

        // 1. Sessions
        List<Session> sessions = sessionRepository.findAll();

        // 2. Séances
        List<Seance> seances = seanceRepository.findAll();

        // 3. Contrôles présents validés
        List<Controle> controles = controleRepository.findAll()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getPresent()) &&
                        Boolean.TRUE.equals(c.getJustifie()) &&
                        Boolean.FALSE.equals(c.getSeance().getIsActive()))
                .toList();

        // 4. Absences non justifiées
        List<Controle> absences = controleRepository.findAll()
                .stream()
                .filter(c -> Boolean.FALSE.equals(c.getPresent()) &&
                        Boolean.FALSE.equals(c.getJustifie()) &&
                        Boolean.FALSE.equals(c.getSeance().getIsActive()))
                .toList();

        // 5. Documents liés aux absences uniquement
        List<Document> documents = documentRepository.findAll()
                .stream()
                .filter(d -> {

                    Controle c = d.getControle();

                    if (c == null || c.getSeance() == null)
                        return false;

                    return Boolean.FALSE.equals(c.getPresent())
                            && Boolean.FALSE.equals(c.getJustifie())
                            && Boolean.FALSE.equals(c.getSeance().getIsActive())
                            && d.getImageUrl() != null
                            && !d.getImageUrl().isEmpty();
                })
                .toList();

        return SyncPayload.builder()
                .sessions(sessions)
                .seances(seances)
                .controles(controles)
                .absences(absences)
                .documents(documents)
                .build();
    }
}