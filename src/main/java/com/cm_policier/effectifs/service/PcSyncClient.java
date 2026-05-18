package com.cm_policier.effectifs.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.PcSyncLoginDTO;
import com.cm_policier.effectifs.dto.SyncResponseDTO;

@Service
public class PcSyncClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "http://10.26.176.185:8090";

    public SyncResponseDTO sync(PcSyncLoginDTO request) {

        String url = BASE_URL + "/api/pc/sync";

        ResponseEntity<ApiResponse> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        ApiResponse.class
                );

        if (response.getBody() == null || response.getBody().getData() == null) {
            throw new RuntimeException("Erreur sync: réponse vide");
        }

        // ✔ conversion SAFE sans ObjectMapper
        Object data = response.getBody().getData();

        return convert(data);
    }

    private SyncResponseDTO convert(Object data) {
        com.fasterxml.jackson.databind.ObjectMapper mapper =
                new com.fasterxml.jackson.databind.ObjectMapper()
                        .findAndRegisterModules(); // ✔ FIX LocalDateTime

        return mapper.convertValue(data, SyncResponseDTO.class);
    }
}