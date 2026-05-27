package com.cm_policier.effectifs.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

  List<Document> findByControle(Controle controle);

  void deleteByControle(Controle controle);

  Optional<Document> findByImageUrl(String imageUrl);

  @Query("""
      SELECT d
      FROM Document d
      WHERE d.controle.seance.isActive = false
      """)
  List<Document> findReadyForSync(Pageable pageable);

  @Query("""
      SELECT COUNT(d)
      FROM Document d
      WHERE d.controle.seance.id = :seanceId
      AND d.controle.seance.isActive = false
      """)
  Long countDocumentsToSync(UUID seanceId);

  Optional<Document> findByFileHash(String fileHash);
}