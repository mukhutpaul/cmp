package com.cm_policier.effectifs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cm_policier.effectifs.dto.SyncBatchRequest;
import com.cm_policier.effectifs.dto.SyncBatchResponse;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.Session;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.DocumentRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.SessionRepository;
import com.cm_policier.effectifs.syncDto.ControleDto;
import com.cm_policier.effectifs.syncDto.DocumentDto;
import com.cm_policier.effectifs.syncDto.SeanceDto;
import com.cm_policier.effectifs.syncDto.SessionDto;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SyncBatchService {

    private final ControleRepository controleRepo;
    private final DocumentRepository documentRepo;
    private final SessionRepository sessionRepo;
    private final SeanceRepository seanceRepo;

    private final String BASE_DIR = "C:/bdd/document/";

    public SyncBatchResponse process(
            SyncBatchRequest dto,
            List<MultipartFile> files) {

        Instant start = Instant.now();

        int sessions = syncSessions(dto.getSessions());
        int seances = syncSeances(dto.getSeances());
        int controles = syncControles(dto.getControles());
        int docs = syncDocuments(dto.getDocuments(), files);

        Instant end = Instant.now();

        return SyncBatchResponse.builder()
                .sessionsSynced(sessions)
                .seancesSynced(seances)
                .controlesSynced(controles)
                .documentsSynced(docs)
                .filesSaved(docs)
                .status("SUCCESS")
                .durationMs(Duration.between(start, end).toMillis())
                .build();
    }

    private int syncSessions(List<SessionDto> sessions) {

    if (sessions == null || sessions.isEmpty())
        return 0;

    int count = 0;

    for (SessionDto dto : sessions) {

        sessionRepo.findById(dto.getId())
                .ifPresentOrElse(existing -> {

                    // =========================
                    // VERSION CHECK (IMPORTANT)
                    // =========================
                    if (dto.getVersionSync() == null ||
                        dto.getVersionSync() <= existing.getVersionSync()) {
                        return;
                    }

                    // =========================
                    // METIER
                    // =========================
                    existing.setDateSession(dto.getDateSession());
                    existing.setHeureDebut(dto.getHeureDebut());
                    existing.setHeureFin(dto.getHeureFin());
                    existing.setIsActive(dto.getIsActive());

                    // =========================
                    // SYNC META
                    // =========================
                    existing.setIsSynchronized(
                            dto.getIsSynchronized() != null ? dto.getIsSynchronized() : true
                    );

                    sessionRepo.save(existing);

                }, () -> {

                    Session s = new Session();

                    s.setId(dto.getId());

                    // =========================
                    // METIER
                    // =========================
                    s.setDateSession(dto.getDateSession());
                    s.setHeureDebut(dto.getHeureDebut());
                    s.setHeureFin(dto.getHeureFin());
                    s.setIsActive(dto.getIsActive());

                    // =========================
                    // SYNC META
                    // =========================
                    s.setIsSynchronized(true);

                    sessionRepo.save(s);
                });

        count++;
    }

    return count;
}
    private int syncSeances(List<SeanceDto> seances) {

    if (seances == null || seances.isEmpty())
        return 0;

    int count = 0;

    for (SeanceDto dto : seances) {

        seanceRepo.findById(dto.getId())
                .ifPresentOrElse(existing -> {

                    // =========================
                    // VERSION CHECK (IMPORTANT)
                    // =========================
                    if (dto.getVersionSync() == null ||
                        dto.getVersionSync() <= existing.getVersionSync()) {
                        return;
                    }

                    // =========================
                    // METIER
                    // =========================
                    existing.setDateSeance(dto.getDateSeance());
                    existing.setDateFin(dto.getDateFin());
                    existing.setIsActive(dto.getIsActive());

                    // =========================
                    // SYNC META
                    // =========================
                    existing.setVersionSync(dto.getVersionSync());
                    existing.setIsSynchronized(dto.getIsSynchronized() != null ? dto.getIsSynchronized() : true);
                    existing.setSyncedAt(dto.getSyncedAt());
                    existing.setUpdatedAt(LocalDateTime.now());

                    seanceRepo.save(existing);

                }, () -> {

                    Seance s = new Seance();

                    s.setId(dto.getId());

                    // =========================
                    // METIER
                    // =========================
                    s.setDateSeance(dto.getDateSeance());
                    s.setDateFin(dto.getDateFin());
                    s.setIsActive(dto.getIsActive());

                    // =========================
                    // SYNC META
                    // =========================
                    s.setVersionSync(dto.getVersionSync() != null ? dto.getVersionSync() : 1);
                    s.setIsSynchronized(true);
                    s.setSyncedAt(dto.getSyncedAt());
                    s.setUpdatedAt(dto.getUpdatedAt());

                    seanceRepo.save(s);
                });

        count++;
    }

    return count;
}

    private int syncControles(List<ControleDto> controles) {

    if (controles == null || controles.isEmpty())
        return 0;

    int count = 0;

    for (ControleDto dto : controles) {

        controleRepo.findByUid(dto.getUid())
                .ifPresentOrElse(existing -> {

                    // =========================
                    // VERSION CHECK (IMPORTANT)
                    // =========================
                    if (dto.getVersionSync() == null ||
                        dto.getVersionSync() <= existing.getVersionSync()) {
                        return;
                    }

                    // =========================
                    // INFOS METIER
                    // =========================
                    existing.setNoms(dto.getNoms());
                    existing.setPresent(dto.getPresent());
                    existing.setJustifie(dto.getJustifie());
                    existing.setObservation(dto.getObservation());
                    existing.setIsControle(dto.getIsControle());
                    existing.setMatricule(dto.getMatricule());
                    existing.setUnite(dto.getUnite());
                    existing.setGrade(dto.getGrade());
                    existing.setSexe(dto.getSexe());

                    // =========================
                    // BIOMETRIE
                    // =========================
                    existing.setFingerprint(dto.getFingerprint());
                    existing.setFingerprint4(dto.getFingerprint4());

                    // =========================
                    // SYNC
                    // =========================
                    existing.setIsSync(true);
                    existing.setVersionSync(dto.getVersionSync());
                    existing.setLastModified(dto.getLastModified());
                    existing.setDeviceId(dto.getDeviceId());
                    existing.setPkPhoto(dto.getPkPhoto());

                    existing.setSyncedAt(LocalDateTime.now());

                    controleRepo.save(existing);

                }, () -> {

                    Controle c = new Controle();

                    c.setUid(dto.getUid());

                    // =========================
                    // INFOS METIER
                    // =========================
                    c.setNoms(dto.getNoms());
                    c.setPresent(dto.getPresent());
                    c.setJustifie(dto.getJustifie());
                    c.setObservation(dto.getObservation());
                    c.setIsControle(dto.getIsControle());
                    c.setMatricule(dto.getMatricule());
                    c.setUnite(dto.getUnite());
                    c.setGrade(dto.getGrade());
                    c.setSexe(dto.getSexe());

                    // =========================
                    // BIOMETRIE
                    // =========================
                    c.setFingerprint(dto.getFingerprint());
                    c.setFingerprint4(dto.getFingerprint4());

                    // =========================
                    // RELATIONS ID (lazy mapping futur)
                    // =========================
                    // à compléter si besoin :
                    // c.setPolicier(...)
                    // c.setSeance(...)

                    // =========================
                    // SYNC
                    // =========================
                    c.setIsSync(true);
                    c.setVersionSync(dto.getVersionSync() != null ? dto.getVersionSync() : 1);
                    c.setLastModified(dto.getLastModified());
                    c.setDeviceId(dto.getDeviceId());
                    c.setPkPhoto(dto.getPkPhoto());

                    c.setSyncedAt(LocalDateTime.now());

                    controleRepo.save(c);
                });

        count++;
    }

    return count;
}
private int syncDocuments(
        List<DocumentDto> docs,
        List<MultipartFile> files) {

    if (docs == null || docs.isEmpty())
        return 0;

    try {

        Path dir = Paths.get(BASE_DIR);

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        int count = 0;

        for (DocumentDto dto : docs) {

            MultipartFile file = null;

            if (files != null && count < files.size()) {
                file = files.get(count);
            }

            if (file == null)
                continue;

            String extension = "";

            String originalName = file.getOriginalFilename();

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String fileName =
                    dto.getId() + "_" + System.currentTimeMillis() + extension;

            Path target = dir.resolve(fileName);

            Files.copy(file.getInputStream(), target,
                    StandardCopyOption.REPLACE_EXISTING);

            Document d = new Document();
            d.setId(dto.getId());
            d.setTitle(dto.getTitle());
            d.setDescription(dto.getDescription());
            d.setImageUrl(fileName);

            // ⚠️ IMPORTANT : à corriger côté service
            // d.setControle(findByUid(dto.getControleUid()));

            d.setSyncedAt(LocalDateTime.now());

            documentRepo.save(d);

            count++;
        }

        return count;

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
}