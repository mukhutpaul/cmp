package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.SyncBatchRequest;
import com.cm_policier.effectifs.dto.SyncBatchResponse;
import com.cm_policier.effectifs.dto.SyncPayload;
import com.cm_policier.effectifs.dto.SyncStatsDto;
import com.cm_policier.effectifs.service.SyncBatchService;
import com.cm_policier.effectifs.service.SyncLocalService;
import com.cm_policier.effectifs.service.SyncStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SyncController {

        private final SyncStatsService statsService;
        private final SyncBatchService syncBatchService;

        // =========================
        // STATS
        // =========================
        @GetMapping("/stats")
        public SyncStatsDto stats() {
                return statsService.stats();
        }

        // =========================
        // SYNC BATCH
        // =========================
        @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<SyncBatchResponse> syncBatch(

                        @RequestPart("data") SyncBatchRequest dto,

                        @RequestPart(value = "files", required = false) List<MultipartFile> files

        ) {

                if (dto == null) {
                        return ResponseEntity.badRequest()
                                        .body(SyncBatchResponse.builder()
                                                        .status("ERROR")
                                                        .build());
                }

                List<MultipartFile> safeFiles = (files != null) ? files : new ArrayList<>();

                SyncBatchResponse response = syncBatchService.process(dto, safeFiles);

                return ResponseEntity.ok(response);
        }

        private final SyncLocalService syncLocalService;

        @PostMapping("/run")
        public ResponseEntity<String> runSync() {
                syncLocalService.executeSync();
                return ResponseEntity.ok("SYNC STARTED");
        }

        private final SyncLocalService syncService;

        @PostMapping("/sync/import")
        public ResponseEntity<?> importSync(@RequestBody SyncPayload payload) {

                syncService.process(payload);

                return ResponseEntity.ok("SYNC OK");
        }
}