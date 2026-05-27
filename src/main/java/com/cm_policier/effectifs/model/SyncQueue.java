package com.cm_policier.effectifs.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
class SyncQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    UUID id;

    String entityType;

    UUID entityId;

    Integer retryCount;

    String errorMessage;

    Boolean synced;
}