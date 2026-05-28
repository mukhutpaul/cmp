package com.cm_policier.effectifs.controllers;

import com.cm_policier.effectifs.dto.SyncBatchRequest;
import com.cm_policier.effectifs.dto.SyncBatchResponse;
import com.cm_policier.effectifs.dto.SyncStatsDto;
import com.cm_policier.effectifs.service.SyncBatchService;
import com.cm_policier.effectifs.service.SyncStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/api_pc_central/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncStatsService statsService;
    private final SyncBatchService syncBatchService;

    // =========================
    // STATS
    // =========================
    @GetMapping("/stats/{seanceId}")
    public SyncStatsDto stats(
            @PathVariable UUID seanceId,
            @RequestParam Boolean active
    ) {
        return statsService.stats(seanceId, active);
    }

    // =========================
    // SYNC BATCH
    // =========================
    @PostMapping(
            value = "/batch",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<SyncBatchResponse> syncBatch(

            @RequestPart("data")
            SyncBatchRequest dto,

            @RequestPart(value = "files", required = false)
            List<MultipartFile> files

    ) {

        // =========================
        // VALIDATION DTO
        // =========================
        if (dto == null) {
            return ResponseEntity.badRequest()
                    .body(SyncBatchResponse.builder()
                            .status("ERROR")
                            .build());
        }

        // =========================
        // SAFE FILES (ANTI NULL)
        // =========================
        List<MultipartFile> safeFiles =
                (files == null) ? new ArrayList<>() : files;

        // =========================
        // CALL SERVICE CORRECT
        // =========================
        SyncBatchResponse response =
                syncBatchService.process(dto, safeFiles);

        return ResponseEntity.ok(response);
    }
}