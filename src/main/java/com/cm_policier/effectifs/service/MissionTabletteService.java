package com.cm_policier.effectifs.service;



import com.cm_policier.effectifs.model.MissionTablette;
import com.cm_policier.effectifs.repository.MissionTabletteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionTabletteService {

    private final MissionTabletteRepository repository;

    public MissionTablette create(MissionTablette m) {
        m.setIsActive(true);
        return repository.save(m);
    }

    public List<MissionTablette> getAll() {
        return repository.findAll();
    }

    public List<MissionTablette> getActive() {
        return repository.findByIsActiveTrue();
    }

    public MissionTablette getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MissionTablette introuvable"));
    }

    public MissionTablette update(Long id, MissionTablette data) {
        MissionTablette m = getById(id);

        m.setMission(data.getMission());
        m.setTablette(data.getTablette());
        m.setIsActive(data.getIsActive());

        return repository.save(m);
    }

    public void delete(Long id) {
        MissionTablette m = getById(id);
        m.setIsActive(false); // soft delete
        repository.save(m);
    }
}