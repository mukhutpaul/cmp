package com.cm_policier.effectifs.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

     List<Document> findByControle(Controle controle);

    void deleteByControle(Controle controle);
}