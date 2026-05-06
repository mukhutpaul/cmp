package com.cm_policier.effectifs.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.repository.EquipeRepository;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    public Equipe create(Equipe equipe) {
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

        return equipeRepository.save(existing);
    }

    public void delete(Long id) {
        equipeRepository.deleteById(id);
    }
}
