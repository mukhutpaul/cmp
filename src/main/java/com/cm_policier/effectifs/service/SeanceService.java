package com.cm_policier.effectifs.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.SeanceRequest;
import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.UserRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class SeanceService {

    
    private final SeanceRepository seanceRepository;
 
    private final UserRepository userRepository;

   
    private MissionRepository missionRepository;
  
    private final LogUserService logUserService;
    
    private final UserService userService;


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

        logUserService.saveLog(user, "Création de la séance");

        return seanceRepository.save(s);
    }

    public Seance getById(UUID id) {
        return seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seance not found"));
    }

    public Seance update(UUID id, Seance seance) {
        Seance existing = getById(id);

        existing.setDateSeance(seance.getDateSeance());
        existing.setDateFin(seance.getDateFin());
        existing.setChefEquipe(seance.getChefEquipe());
        existing.setMission(seance.getMission());
        existing.setIsActive(seance.getIsActive());

        return seanceRepository.save(existing);
    }

    public void delete(UUID id) {

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
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Suppression de la séance");
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

    public Seance start(UUID id) {
        Seance s = getById(id);

        if (Boolean.TRUE.equals(s.getIsActive())) {
            throw new RuntimeException("Séance déjà active");
        }

        s.setIsActive(true);
        s.setDateSeance(LocalDateTime.now());
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Activation de la séance");

        return seanceRepository.save(s);
    }

    public Seance getActiveSeance() {
        return seanceRepository.findFirstByIsActiveTrueAndDateFinIsNull()
                .orElse(null);
    }

    public Seance finish(UUID id) {

        Seance s = getById(id);

        if (!Boolean.TRUE.equals(s.getIsActive())) {
            throw new RuntimeException("Séance pas active");
        }

        // fermeture séance
        s.setIsActive(false);

        // date réelle de fin

        
        s.setDateFin(LocalDateTime.now());
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Clôture de la séance");

        return seanceRepository.save(s);
    }

    public List<Seance> collectSeances() {
        return seanceRepository.findAll().stream()
                .filter(s -> s.getIsActive() || !s.getIsSynchronized())
                .toList();
    }

}