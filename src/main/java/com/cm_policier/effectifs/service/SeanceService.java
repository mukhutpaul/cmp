package com.cm_policier.effectifs.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.SeanceRequest;
import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.UserRepository;

@Service
public class SeanceService {

    @Autowired
    private SeanceRepository seanceRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    public Seance create(SeanceRequest request) {

        User user = userRepository.findById(request.getChefEquipeId())
                .orElseThrow(() -> new RuntimeException("User introuvable"));

        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission introuvable"));

        Seance s = new Seance();

        s.setChefEquipe(user);
        s.setMission(mission);
        s.setDateSeance(LocalDateTime.now());
        s.setDateFin(null);
        s.setIsActive(false);

        return seanceRepository.save(s);
    }

    public Seance getById(Long id) {
        return seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seance not found"));
    }

    public Seance update(Long id, Seance seance) {
        Seance existing = getById(id);

        existing.setDateSeance(seance.getDateSeance());
        existing.setDateFin(seance.getDateFin());
        existing.setChefEquipe(seance.getChefEquipe());
        existing.setMission(seance.getMission());
        existing.setIsActive(seance.getIsActive());

        return seanceRepository.save(existing);
    }

  public void delete(Long id) {

    Seance seance = seanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Séance introuvable"));

    // 🚫 BLOQUAGE : séance active
    if (Boolean.TRUE.equals(seance.getIsActive())) {
        throw new RuntimeException("Impossible de supprimer une séance active");
    }

    // 🚫 BLOQUAGE : séance clôturée
    if (seance.getDateFin() != null) {
        throw new RuntimeException("Impossible de supprimer une séance clôturée");
    }

    seanceRepository.deleteById(id);
}

    public List<Seance> getByMission(Long missionId) {
        return seanceRepository.findByMissionId(missionId);
    }

    public List<Seance> getByChef(Long chefId) {
        return seanceRepository.findByChefEquipeId(chefId);
    }

    public List<Seance> getAll() {
        return seanceRepository.findAll();
    }

    public Seance start(Long id) {
        Seance s = getById(id);

        if (Boolean.TRUE.equals(s.getIsActive())) {
            throw new RuntimeException("Séance déjà active");
        }

        s.setIsActive(true);
        s.setDateSeance(LocalDateTime.now());

        return seanceRepository.save(s);
    }

    public Seance finish(Long id) {

        Seance s = getById(id);

        if (!Boolean.TRUE.equals(s.getIsActive())) {
            throw new RuntimeException("Séance pas active");
        }

        // fermeture séance
        s.setIsActive(false);

        // date réelle de fin
        s.setDateFin(LocalDateTime.now());

        return seanceRepository.save(s);
    }

}