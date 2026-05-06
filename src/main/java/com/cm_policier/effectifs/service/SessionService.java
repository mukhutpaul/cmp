package com.cm_policier.effectifs.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.Session;
import com.cm_policier.effectifs.repository.SessionRepository;

@Service
public class SessionService {

    @Autowired
    private SessionRepository repository;

    public Session create(Session session) {
        return repository.save(session);
    }

    public List<Session> getAll() {
        return repository.findAll();
    }

    public Session getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    public Session update(Long id, Session session) {
        Session existing = getById(id);

        existing.setDateSession(session.getDateSession());
        existing.setHeureDebut(session.getHeureDebut());
        existing.setHeureFin(session.getHeureFin());
        existing.setControleur(session.getControleur());
        existing.setSeance(session.getSeance());
        existing.setIsSynchronized(session.getIsSynchronized());
        existing.setIsActive(session.getIsActive());

        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}