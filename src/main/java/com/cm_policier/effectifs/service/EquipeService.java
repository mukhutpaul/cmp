package com.cm_policier.effectifs.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class EquipeService {


    private final EquipeRepository equipeRepository;

    private final LogUserService logUserService;

    private final UserService userService;

    public Equipe create(Equipe equipe) {
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Création de l'équipe:"+equipe.getUser().getUsername());
        return equipeRepository.save(equipe);
        
    }

    public List<Equipe> getAll() {
        return equipeRepository.findAllByOrderByIdDesc();
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
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Modification de l'équipe:"+equipe.getUser().getUsername());

        return equipeRepository.save(existing);
    }

    public void delete(Long id) {
        equipeRepository.deleteById(id);
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Suppression de l'équipe:"+id);
    }

    public Equipe findByChef(Long chefId) {

        return equipeRepository.findByUser_Id(chefId)
                .orElseThrow(() -> new RuntimeException("Equipe introuvable"));
    }
}
