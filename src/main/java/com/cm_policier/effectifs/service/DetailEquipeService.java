package com.cm_policier.effectifs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.model.DetailEquipe;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.DetailEquipeRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;

@Service
public class DetailEquipeService {

    @Autowired
    private DetailEquipeRepository repository;
    @Autowired
    private LogUserService logUserService;
    @Autowired
    private UserService userService;

    public DetailEquipe create(DetailEquipe detail) {

        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);

        String userAjoute = (detail.getUser() != null)
                ? detail.getUser().getUsername()
                : "UNKNOWN_USER";

        String equipeUser = (detail.getEquipe() != null && detail.getEquipe().getUser() != null)
                ? detail.getEquipe().getUser().getUsername()
                : "UNKNOWN_EQUIPE_USER";

        logUserService.saveLog(
                user,
                "Ajout contrôleur: " + userAjoute +
                        " dans l'équipe: " + equipeUser);

        return repository.save(detail);
    }

    public List<DetailEquipe> getAll() {
        return repository.findAll();
    }

    public DetailEquipe getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetailEquipe not found"));
    }

    public DetailEquipe update(Long id, DetailEquipe detail) {
        DetailEquipe existing = getById(id);

        existing.setEquipe(detail.getEquipe());
        existing.setUser(detail.getUser());
        existing.setIsActive(detail.getIsActive());

        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Suppression contôleur équipe:" + id);
    }

    public List<DetailEquipe> findByEquipe(Long equipeId) {
        return repository.findByEquipeId(equipeId);
    }

}
