package com.cm_policier.effectifs.model;

import java.util.UUID;

import jakarta.persistence.Entity;

@Entity
class SyncQueue {

   UUID id;

   String entityType;

   UUID entityId;

   Integer retryCount;

   String errorMessage;

   Boolean synced;
}