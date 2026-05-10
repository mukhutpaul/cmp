package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.repository.ControleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ControleService {

    private final ControleRepository repository;

    /* ========================= CREATE ========================= */
    public Controle create(Controle controle) {
        controle.setCreatedAt(java.time.LocalDateTime.now());
        return repository.save(controle);
    }

    /* ========================= READ ALL (PAGINATION) ========================= */
    public Page<Controle> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    /* ========================= READ ONE ========================= */
    public Controle getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Controle introuvable"));
    }

    /* ========================= UPDATE ========================= */
    public Controle update(Long id, Controle data) {
        Controle c = getById(id);

        c.setUid(data.getUid());
        c.setPerson(data.getPerson());
        c.setPresent(data.getPresent());
        c.setJustifie(data.getJustifie());
        c.setSituation(data.getSituation());
        c.setStatus(data.getStatus());
        c.setMatricule(data.getMatricule());
        c.setUnite(data.getUnite());
        c.setGrade(data.getGrade());
        c.setIsActif(data.getIsActif());

        return repository.save(c);
    }

    /* ========================= DELETE ========================= */
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
