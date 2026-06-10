package com.cm_policier.effectifs.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.util.getCurrentUser;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private LogUserService logUserService;

    User user = getCurrentUser.getCurrentUser();

    public Equipe create(Equipe equipe) {
        logUserService.saveLog(user, "Création de l'équipe:"+equipe.getUser().getUsername());
        return equipeRepository.save(equipe);
        
    }

    public List<Equipe> getAll() {
        return equipeRepository.findAll();
    }

    public Equipe getById(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));
    }

    public Equipe update(Long id, Equipe equipe) {
        Equipe existing = getById(id);

        existing.setUser(equipe.getUser());
        existing.setMission(equipe.getMission());
        existing.setIsActive(equipe.getIsActive());
        logUserService.saveLog(user, "Modification de l'équipe:"+equipe.getUser().getUsername());

        return equipeRepository.save(existing);
    }

    public void delete(Long id) {
        equipeRepository.deleteById(id);
        logUserService.saveLog(user, "Suppression de l'équipe:"+id);
    }

    public Equipe findByChef(Long chefId) {

        return equipeRepository.findByUser_Id(chefId)
                .orElseThrow(() -> new RuntimeException("Equipe introuvable"));
    }
}
