package com.cm_policier.effectifs.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.service.ControleServiceLoader;

@RestController
@RequestMapping("/api/mobile")
public class ControleControllerLoader {

    @Autowired
    private ControleServiceLoader controleService;

    @PostMapping("/charger")
    public ResponseEntity<?> charger(
            @RequestParam String unite,
            @RequestParam Long userId
    ) {

        List<Controle> result = controleService.chargerControle(unite, userId);

        return ResponseEntity.ok(result);
    }

}
