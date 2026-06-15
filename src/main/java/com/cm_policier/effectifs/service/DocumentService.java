package com.cm_policier.effectifs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.DocumentSyncDTO;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Document;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.DocumentRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;
    private final  LogUserService logUserService;
    private final UserService userService;

    public Document create(Document document) {
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Création document:" + document.getTitle());
        return repository.save(document);

    }

    public List<Document> getAll() {
        return repository.findAll();
    }

    public Document getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public Document update(UUID id, Document document) {
        Document existing = getById(id);

        existing.setTitle(document.getTitle());
        existing.setDescription(document.getDescription());
        existing.setImageUrl(document.getImageUrl());
        existing.setControle(document.getControle());

        return repository.save(existing);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public List<DocumentSyncDTO> collectDocuments(List<Controle> controles) {

        List<Document> docs = repository.findAll().stream()
                .filter(d -> !d.getControle().getSeance().getIsActive())
                .toList();

        return docs.stream()
                .map((Document d) -> new DocumentSyncDTO(
                        d.getId(),
                        d.getControle().getId(),
                        d.getTitle(),
                        encodeFile(d.getImageUrl())))
                .collect(Collectors.toList());
    }

    public String encodeFile(String fileName) {
        try {
            Path path = Paths.get("C:/bdd/document/" + fileName);
            byte[] bytes = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Image error");
        }
    }

    public void saveImage(String base64, String fileName) throws IOException {

        byte[] bytes = Base64.getDecoder().decode(base64);

        Path path = Paths.get("C:/bdd/document/" + fileName);

        Files.createDirectories(path.getParent());

        Files.write(path, bytes);
    }

}