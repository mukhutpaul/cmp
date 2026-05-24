package com.cm_policier.effectifs.controllers;


import com.cm_policier.effectifs.dto.StatMissionDto;
import com.cm_policier.effectifs.service.StatMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StatMissionController {

    private final StatMissionService statMissionService;

    @GetMapping("/stats/missions")
    public List<StatMissionDto> stats() {
        return statMissionService.getStats();
    }
}