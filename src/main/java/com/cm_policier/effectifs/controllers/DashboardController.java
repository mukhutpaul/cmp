package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.DashboardStatsDTO;
import com.cm_policier.effectifs.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStatsDTO getStats(
            @RequestParam String profile,
            @RequestParam Long userId) {
        return dashboardService.getStats(profile, userId);
    }
}
