package com.cm_policier.effectifs.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.UserRepository;

@Service
public class SeanceService {

    @Autowired
    private SeanceRepository seanceRepository;
    @Autowired
    private UserRepository userRepository;

    public Seance create(Seance seance, String username) {

        User user = userRepository.findByUsername(username).
        orElseThrow(() -> new RuntimeException("User introuvable"));;

        seance.setChefEquipe(user);
        seance.setDateSeance(LocalDate.now());

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