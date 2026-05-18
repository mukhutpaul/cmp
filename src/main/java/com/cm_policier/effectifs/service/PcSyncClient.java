package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.PcSyncLoginDTO;
import com.cm_policier.effectifs.dto.SyncResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PcSyncClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private static final String BASE_URL = "http://10.26.176.185:8090";

    public SyncResponseDTO sync(PcSyncLoginDTO request) {

        String url = BASE_URL + "/api/pc/sync";

        ResponseEntity<ApiResponse> response =
                restTemplate.postForEntity(url, request, ApiResponse.class);

        if (response.getBody() == null || response.getBody().getData() == null) {
            throw new RuntimeException("Erreur sync: réponse vide");
        }

        return mapper.convertValue(
                response.getBody().getData(),
                SyncResponseDTO.class
        );
    }
}