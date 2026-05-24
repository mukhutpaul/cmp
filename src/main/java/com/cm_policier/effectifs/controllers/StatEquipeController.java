package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.StatEquipeDto;
import com.cm_policier.effectifs.service.StatEquipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatEquipeController {

    private final StatEquipeService statEquipeService;

    @GetMapping("/stats/equipes")
    public List<StatEquipeDto> stats() {
        return statEquipeService.getStats();
    }
}