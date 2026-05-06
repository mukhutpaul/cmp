package com.cm_policier.effectifs.service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.repository.DocumentRepository;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository repository;

    public Document create(Document document) {
        return repository.save(document);
    }

    public List<Document> getAll() {
        return repository.findAll();
    }

    public Document getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public Document update(Long id, Document document) {
        Document existing = getById(id);

        existing.setTitle(document.getTitle());
        existing.setDescription(document.getDescription());
        existing.setImageUrl(document.getImageUrl());
        existing.setControle(document.getControle());

        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}