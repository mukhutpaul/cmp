package com.cm_policier.effectifs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.PolicierRepository;

@Service
public class PolicierService {

    @Autowired
    private PolicierRepository repository;

    public Policier ajouter(Policier p) {
        return repository.save(p);
    }

    public List<Policier> lister() {
        return repository.findAll();
    }
}