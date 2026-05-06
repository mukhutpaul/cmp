package com.cm_policier.effectifs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cm_policier.effectifs.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}