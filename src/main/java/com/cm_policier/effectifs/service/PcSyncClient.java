package com.cm_policier.effectifs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.PcSyncLoginDTO;
import com.cm_policier.effectifs.dto.SyncResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PcSyncClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public SyncResponseDTO sync(PcSyncLoginDTO request) {

        // ================= URL DYNAMIQUE =================
        String url = request.getBaseUrl() + "/api/pc/sync";

        // ================= CALL API =================
        ResponseEntity<ApiResponse> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        ApiResponse.class
                );

        // ================= VALIDATION =================
        if (response.getBody() == null || response.getBody().getData() == null) {
            throw new RuntimeException("Erreur sync: réponse vide");
        }

        // ================= MAPPING =================
        return mapper.convertValue(
                response.getBody().getData(),
                SyncResponseDTO.class
        );
    }
}