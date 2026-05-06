package com.cm_policier.effectifs.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.repository.SeanceRepository;

@Service
public class SeanceService {

    @Autowired
    private SeanceRepository seanceRepository;

    public Seance create(Seance seance) {
        return seanceRepository.save(seance);
    }

    public List<Seance> getAll() {
        return seanceRepository.findAll();
    }

    public Seance getById(Long id) {
        return seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seance not found"));
    }

    public Seance update(Long id, Seance seance) {
        Seance existing = getById(id);

        existing.setDateSeance(seance.getDateSeance());
        existing.setHeureDebut(seance.getHeureDebut());
        existing.setHeureFin(seance.getHeureFin());
        existing.setChefEquipe(seance.getChefEquipe());
        existing.setMission(seance.getMission());
        existing.setIsActive(seance.getIsActive());

        return seanceRepository.save(existing);
    }

    public void delete(Long id) {
        seanceRepository.deleteById(id);
    }

    public List<Seance> getByMission(Long missionId) {
    return seanceRepository.findByMissionId(missionId);
}

public List<Seance> getByChef(Long chefId) {
    return seanceRepository.findByChefEquipeId(chefId);
}

}