package com.cm_policier.effectifs.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.Session;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.SessionRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;


@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private  LogUserService logUserService;
    @Autowired
    private  UserService userService;

    /**
     * 🟢 CREATE SESSION
     */
    public Session createSession(User controleur, Seance seance) {

        Session session = Session.builder()
                .controleur(controleur)
                .seance(seance)
                .isActive(true)
                .isSynchronized(false)
                .dateSession(LocalDate.now())
                .heureDebut(LocalTime.now())
                .build();
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Création session");
        return sessionRepository.save(session);
    }

    /**
     * 🔵 GET ACTIVE SESSION GLOBAL
     */
    public Session getActiveSession() {
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Activation de la séance");
        return sessionRepository.findByIsActiveTrue()
                .orElse(null);
    }

    /**
     * 🔴 CLOSE SESSION BY ID
     */
    public Session closeSession(UUID sessionId) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session introuvable"));

        if (!Boolean.TRUE.equals(session.getIsActive())) {
            throw new RuntimeException("Session déjà fermée");
        }

        session.setIsActive(false);
        session.setHeureFin(LocalTime.now());
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "clôture de la session");

        return sessionRepository.save(session);
    }

    /**
     * 🔴 CLOSE SESSION BY USER
     */
    public List<Session> collectSessions(List<Seance> seances) {

        return sessionRepository.findAll().stream()
                .filter(s -> seances.stream()
                        .anyMatch(seance -> seance.getId().equals(s.getSeance().getId())))
                .collect(Collectors.toList());
    }
}