package com.cm_policier.effectifs.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cm_policier.effectifs.dto.SyncPayload;

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