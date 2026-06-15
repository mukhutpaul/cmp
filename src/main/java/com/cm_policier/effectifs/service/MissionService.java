package com.cm_policier.effectifs.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;

    private final LogUserService logUserService;

    private final UserService userService;

    public Mission create(Mission mission) {
        mission.setIsActive(false);
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Création mission:"+mission.getZone());
        return missionRepository.save(mission);
    }

    public List<Mission> getAll() {
        return missionRepository.findAllByOrderByIdDesc();
    }

    public Mission getById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found"));
    }

    public Mission update(Long id, Mission mission) {
        Mission existing = getById(id);

        existing.setDateDebut(mission.getDateDebut());
        existing.setDateFin(mission.getDateFin());
        existing.setZone(mission.getZone());
        existing.setNumero(mission.getNumero());
        existing.setChargeMission(mission.getChargeMission());

        return missionRepository.save(existing);
    }

    // public void delete(Long id) {
    // Mission mission = getById(id);
    // mission.setIsActive(false); // soft delete
    // missionRepository.save(mission);
    // }

    public void delete(Long id) {

        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission introuvable"));

        // 🚨 BLOQUAGE MÉTIER
        if (Boolean.TRUE.equals(mission.getIsActive())) {
            throw new RuntimeException("Impossible de supprimer une mission active");
        }

        if (mission.getDateFin() != null) {
            throw new RuntimeException(
                    "Impossible de supprimer une mission déjà terminée");
        }

        missionRepository.delete(mission);
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Suppression mission:"+mission.getZone());
    }

    public Mission startMission(Long id) {

        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission introuvable"));

        if (mission.getIsActive()) {
            throw new RuntimeException("Mission déjà active");
        }

        mission.setIsActive(true);
        mission.setDateDebut(LocalDateTime.now());
        mission.setDateFin(null); // reset sécurité
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Activation mission:"+mission.getZone());
        return missionRepository.save(mission);
    }

    public Mission closeMission(Long id) {

        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission introuvable"));

        if (!mission.getIsActive()) {
            throw new RuntimeException("Mission déjà clôturée");
        }

        mission.setIsActive(false);
        mission.setDateFin(LocalDateTime.now());
        String username = CurrentUserUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        logUserService.saveLog(user, "Fermeture de la mission:"+mission.getZone());

        return missionRepository.save(mission);
    }

    public Mission findMissionByChargeMission(Long userId) {

        return missionRepository.findByChargeMission_Id(userId)
                .orElse(null);
    }

    public Mission findMissionByUser(Long userId) {
        return missionRepository.findByChargeMission_Id(userId)
                .orElse(null);
    }

}