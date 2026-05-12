package com.cm_policier.effectifs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cm_policier.effectifs.dto.SyncPayload;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @Autowired
    private SyncService syncService;

    @PostMapping("/push")
    public ResponseEntity<SyncPayload> syncPush() {

        SyncPayload payload = syncService.buildSyncPayload();

        return ResponseEntity.ok(payload);
    }
}
