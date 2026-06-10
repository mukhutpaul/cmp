package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.DetailUniteRepository;
import com.cm_policier.effectifs.util.getCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetailUniteService {

    private final DetailUniteRepository repository;
     @Autowired
    private LogUserService logUserService;

    User user = getCurrentUser.getCurrentUser();

    public DetailUnite create(DetailUnite d) {
        d.setIsActive(true);
        logUserService.saveLog(user, "Ajout unité au contrôleur:"+d.getUser().getUsername());
        return repository.save(d);
    }

    public List<DetailUnite> getAll() {
        return repository.findAll();
    }

    public List<DetailUnite> getActive() {
        return repository.findByIsActiveTrue();
    }

    public DetailUnite getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetailUnite introuvable"));
    }

    public DetailUnite update(Long id, DetailUnite data) {
        DetailUnite d = getById(id);

        d.setUser(data.getUser());
        d.setUnite(data.getUnite());
        d.setIsActive(data.getIsActive());

        return repository.save(d);
    }

    public void delete(Long id) {

        DetailUnite d = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "DetailUnite introuvable"));

        repository.delete(d);
         logUserService.saveLog(user, "Suppression unité du contrôleur:"+id);
    }

    public List<Unite> getUnitesByUser(Long userId) {

        List<DetailUnite> relations = repository.findByUser_Id(userId);

        return relations.stream()
                .map(DetailUnite::getUnite)
                .toList();
    }

    public List<DetailUnite> findUnitesByEquipe(Long equipeId) {

        return repository.findByEquipe(equipeId);
    }
}