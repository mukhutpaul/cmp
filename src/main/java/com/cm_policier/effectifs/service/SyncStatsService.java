package com.cm_policier.effectifs.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.SyncStatsDto;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.DocumentRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.SessionRepository;


@Service
@RequiredArgsConstructor
public class SyncStatsService {

    private final SessionRepository sessionRepo;
    private final SeanceRepository seanceRepo;
    private final ControleRepository controleRepo;
    private final DocumentRepository documentRepo;

    public SyncStatsDto stats() {

    var seance = seanceRepo.findFirstByOrderByIdAsc()
            .orElseThrow(() -> new RuntimeException("Aucune séance trouvée"));

    boolean seanceActive = Boolean.TRUE.equals(seance.getIsActive());

    Long sessions = sessionRepo.countUnsynchronized();
    Long seances = seanceRepo.countUnsynchronized();

    Long justifies = controleRepo.countJustifie(seance.getId());
    Long presence = controleRepo.countPresenceToSync(seance.getId());

    Long absence = 0L;
    Long documents = 0L;
   

    if (!seanceActive) {
        absence = controleRepo.countAbsenceToSync(seance.getId());
        documents = documentRepo.countDocumentsToSync(seance.getId());
    }

    Long total = sessions
            + seances
            + presence
            + absence
            + documents
            + justifies;

    return SyncStatsDto.builder()
            .sessions(sessions)
            .seances(seances)
            .controlesPresence(presence)
            .controlesJustifies(justifies)
            .controlesAbsence(absence)
            .documents(documents)
            .total(total)
            .seanceActive(seanceActive)
            .build();
}
}