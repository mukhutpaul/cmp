package com.cm_policier.effectifs.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cm_policier.effectifs.dto.ChargerUniteRequest;
import com.cm_policier.effectifs.service.UniteChargeService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/unites")
@RequiredArgsConstructor
public class UniteChargeController {

    private final UniteChargeService uniteChargeService;

    @PostMapping("/charger")
    public ResponseEntity<?> chargerUnite(@RequestBody ChargerUniteRequest request) {
        return ResponseEntity.ok(uniteChargeService.chargerUnite(request));
    }
}