package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.ControleStatsDto;
import com.cm_policier.effectifs.service.ControleStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/controles")
@RequiredArgsConstructor
public class ControleStatsController {

    private final ControleStatsService controleStatsService;

    @GetMapping("/stats/today")
    public ControleStatsDto getTodayStats() {
        return controleStatsService.getTodayStats();
    }

    @GetMapping("/stats")
    public ControleStatsDto getStats() {
        return controleStatsService.getStats();
    }
}