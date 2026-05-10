package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.repository.DetailUniteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetailUniteService {

    private final DetailUniteRepository repository;

    public DetailUnite create(DetailUnite d) {
        d.setIsActive(true);
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
        DetailUnite d = getById(id);
        d.setIsActive(false); // soft delete
        repository.save(d);
    }

    public List<Unite> getUnitesByUser(Long userId) {

    List<DetailUnite> relations =
            repository.findByUserId(userId);

    return relations.stream()
            .map(DetailUnite::getUnite)
            .toList();
}
}