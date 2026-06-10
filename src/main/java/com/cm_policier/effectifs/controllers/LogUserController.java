package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.LogUserResponse;
import com.cm_policier.effectifs.model.LogUser;
import com.cm_policier.effectifs.service.LogUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogUserController {

    private final LogUserService logUserService;

    @GetMapping
    public ResponseEntity<List<LogUserResponse>> getAllLogs() {
        return ResponseEntity.ok(logUserService.getAllLogs());
    }
}