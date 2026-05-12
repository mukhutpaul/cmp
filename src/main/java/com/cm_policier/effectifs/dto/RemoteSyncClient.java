package com.cm_policier.effectifs.dto;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteSyncClient {

    private final RestTemplate restTemplate;

    public RemoteSyncClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void push(SyncPayload payload) {

        restTemplate.postForObject(
                "https://central-api/sync/receive",
                payload,
                Void.class
        );
    }
}