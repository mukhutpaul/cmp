package com.cm_policier.effectifs.service;

import com.cm_policier.effectifs.dto.ControleResponseDto;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.repository.ControleRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ControleService {

    private final ControleRepository repository;

    /* ========================= CREATE ========================= */
    public Controle create(Controle controle) {
        controle.setCreatedAt(java.time.LocalDateTime.now());
        return repository.save(controle);
    }

    /* ========================= READ ALL (PAGINATION) ========================= */
    public Page<Controle> getAll(int page, int size, String search) {

        PageRequest pageable = PageRequest.of(
                page,
                size,
                org.springframework.data.domain.Sort.by("createdAt").descending());

        if (search == null || search.isBlank()) {
            return repository.findAll(pageable);
        }

        return repository.search(search, pageable);
    }

    /* ========================= READ ONE ========================= */
    public Controle getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Controle introuvable"));
    }

    public List<ControleResponseDto> getAll() {

        List<Controle> controles = repository.findAllByOrderByUpdatedAtDesc();

        return controles.stream()
                .map(controle -> {

                    ControleResponseDto dto = ControleResponseDto.builder()

                            // ===================== IDENTIFIANTS =====================

                            .id(controle.getId())
                            .uid(controle.getUid())

                            // ===================== RELATIONS =====================

                            .policierId(
                                    controle.getPolicier() != null
                                            ? controle.getPolicier().getId()
                                            : null)

                            .justificationId(
                                    controle.getJustification() != null
                                            ? controle.getJustification().getId()
                                            : null)

                            .controleurId(
                                    controle.getControleur() != null
                                            ? controle.getControleur().getId()
                                            : null)

                            .seanceId(
                                    controle.getSeance() != null
                                            ? controle.getSeance().getId()
                                            : null)

                            .chefEquipeId(
                                    controle.getChefEquipe() != null
                                            ? controle.getChefEquipe().getId()
                                            : null)

                            .chargeMissionId(
                                    controle.getChargeMission() != null
                                            ? controle.getChargeMission().getId()
                                            : null)

                            .equipeId(
                                    controle.getEquipe() != null
                                            ? controle.getEquipe().getId()
                                            : null)

                            .missionId(
                                    controle.getMission() != null
                                            ? controle.getMission().getId()
                                            : null)

                            // ===================== INFORMATIONS =====================

                            .noms(controle.getNoms())
                            .present(controle.getPresent())
                            .justifie(controle.getJustifie())
                            .observation(controle.getObservation())
                            .isControle(controle.getIsControle())
                            .matricule(controle.getMatricule())
                            .unite(controle.getUnite())
                            .grade(controle.getGrade())
                            .sexe(controle.getSexe())

                            // ===================== BIOMETRIE =====================

                            .fingerprint(controle.getFingerprint())
                            .fingerprint4(controle.getFingerprint4())

                            // ===================== FLAGS =====================

                            .isCmd(controle.getIsCmd())
                            .isActif(controle.getIsActif())
                            .isSync(controle.getIsSync())
                            .versionSync(controle.getVersionSync())

                            // ===================== FILE =====================

                            .qrcode(controle.getQrcode())
                            .province(controle.getProvince())
                            .deviceId(controle.getDeviceId())
                            .pkPhoto(controle.getPkPhoto())

                            // ===================== TIMESTAMPS =====================

                            .syncedAt(controle.getSyncedAt())
                            .createdAt(controle.getCreatedAt())
                            .updatedAt(controle.getUpdatedAt())

                            .build();

                    // ===================== PHOTO URL =====================

                    if (controle.getPkPhoto() != null
                            && !controle.getPkPhoto().isEmpty()) {

                        dto.setPhotoUrl(
                                "http://localhost:8090/photos/"
                                        + controle.getPkPhoto()
                                        + ".jpg");
                    }

                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /* ========================= UPDATE ========================= */
    public Controle update(UUID id, Controle data) {
        Controle c = getById(id);
        c.setPresent(data.getPresent());
        c.setJustifie(data.getJustifie());

        return repository.save(c);
    }

    /* ========================= DELETE ========================= */
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public void findByMatricule(String matricule) {
        repository.findByMatricule(matricule);
    }

    public List<Controle> searchByIdentite(
            String nom,
            String postnom,
            String prenom,
            LocalDate dateNaissance) {
        return repository.searchByPolicierIdentite(
                nom,
                postnom,
                prenom,
                dateNaissance);
    }

    public Optional<Controle> getByMatricule(String matricule) {
        return repository.findByMatricule(matricule);
    }
}
