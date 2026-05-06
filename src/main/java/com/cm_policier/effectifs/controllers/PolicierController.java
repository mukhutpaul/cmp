package com.cm_policier.effectifs.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.service.PolicierService;

@RestController
@RequestMapping("/api/policiers")
public class PolicierController {

    @Autowired
    private PolicierService service;

    @PostMapping
    public Policier ajouter(@RequestBody Policier p) {
        return service.ajouter(p);
    }

    @GetMapping
    public List<Policier> liste() {
        return service.lister();
    }
}