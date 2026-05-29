package com.cm_policier.effectifs.client;

import com.cm_policier.effectifs.dto.SyncBatchRequest;
import com.cm_policier.effectifs.dto.SyncBatchResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
public class RemoteSyncClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public RemoteSyncClient(
            RestTemplate restTemplate,
            @Value("${sync.server.url}") String serverUrl
    ) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public SyncBatchResponse push(
            SyncBatchRequest payload,
            List<MultipartFile> files
    ) {

        try {

            // =========================
            // BODY MULTIPART
            // =========================
            MultiValueMap<String, Object> body =
                    new LinkedMultiValueMap<>();

            // =========================
            // JSON DATA
            // =========================
            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SyncBatchRequest> jsonPart =
                    new HttpEntity<>(payload, jsonHeaders);

            body.add("data", jsonPart);

            // =========================
            // FILES
            // =========================
            if (files != null && !files.isEmpty()) {

                for (MultipartFile file : files) {

                    ByteArrayResource resource =
                            new ByteArrayResource(file.getBytes()) {

                                @Override
                                public String getFilename() {
                                    return file.getOriginalFilename();
                                }
                            };

                    HttpHeaders fileHeaders = new HttpHeaders();

                    fileHeaders.setContentType(
                            MediaType.APPLICATION_OCTET_STREAM
                    );

                    HttpEntity<ByteArrayResource> filePart =
                            new HttpEntity<>(resource, fileHeaders);

                    body.add("files", filePart);
                }
            }

            // =========================
            // GLOBAL HEADERS
            // =========================
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(
                    MediaType.MULTIPART_FORM_DATA
            );

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // =========================
            // REQUEST
            // =========================
            ResponseEntity<SyncBatchResponse> response =
                    restTemplate.exchange(
                            serverUrl,
                            HttpMethod.POST,
                            requestEntity,
                            SyncBatchResponse.class
                    );

            log.info("SYNC SUCCESS : {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {

            log.error("SYNC ERROR", e);

            throw new RuntimeException(
                    "Erreur lors de la synchronisation",
                    e
            );
        }
    }
}