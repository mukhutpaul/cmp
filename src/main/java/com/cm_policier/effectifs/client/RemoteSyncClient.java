package com.cm_policier.effectifs.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cm_policier.effectifs.dto.SyncPayload;

@Service
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

    public void push(SyncPayload payload) {

        restTemplate.postForObject(
                serverUrl,
                payload,
                Void.class
        );
    }
}