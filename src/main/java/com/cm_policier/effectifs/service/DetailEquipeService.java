package com.cm_policier.effectifs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.DetailEquipe;
import com.cm_policier.effectifs.repository.DetailEquipeRepository;

@Service
public class DetailEquipeService {

    @Autowired
    private DetailEquipeRepository repository;

    public DetailEquipe create(DetailEquipe detail) {
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
    }

     public List<DetailEquipe> findByEquipe(Long equipeId) {
        return repository.findByEquipeId(equipeId);
    }

 
}
