package com.cm_policier.effectifs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.client.RemoteSyncClient;
import com.cm_policier.effectifs.dto.SyncPayload;

@Service
public class SyncBatchEngine {

    @Autowired
    private SyncService syncService;

    @Autowired
    private RemoteSyncClient remoteSyncClient;

    public void syncAll() {

        SyncPayload payload = syncService.buildSyncPayload();

        //remoteSyncClient.push(payload);
    }
}