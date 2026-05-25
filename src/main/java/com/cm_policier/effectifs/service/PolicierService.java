package com.cm_policier.effectifs.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import com.cm_policier.effectifs.dto.PolicierDto;
import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.PolicierRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicierService {

    private final PolicierRepository repository;

    // CREATE
    public Policier create(Policier policier) {

        if (repository.existsByMatricule(policier.getMatricule())) {
            throw new RuntimeException("Matricule déjà utilisé");
        }

        return repository.save(policier);
    }

    public List<PolicierDto> getPoliciers() {

        List<Policier> policiers = repository.findAll();

        return policiers.stream().map(p -> {

            PolicierDto dto = new PolicierDto();

            dto.setId(p.getId());
            dto.setMatricule(p.getMatricule());
            dto.setLastname(p.getLastname());
            dto.setPostname(p.getPostname());
            dto.setFirstnames(p.getFirstnames());
            dto.setUnit(p.getUnit());
            dto.setMainUnit(p.getMainUnit());
            dto.setGender(p.getGender());
            dto.setTelephone(p.getTelephone());
            dto.setPkPhoto(p.getPkPhoto());

            if (p.getPkPhoto() != null && !p.getPkPhoto().isEmpty()) {

                dto.setPhotoUrl(
                        "photos/" + p.getPkPhoto() + ".jpg");
            }

            return dto;

        }).toList();
    }

    // READ ONE
    public Policier findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policier introuvable"));
    }

    // UPDATE
    public Policier update(Long id, Policier policier) {

        Policier existing = findById(id);

        existing.setMatricule(policier.getMatricule());
        existing.setLastname(policier.getLastname());
        existing.setPostname(policier.getPostname());
        existing.setFirstnames(policier.getFirstnames());
        existing.setGender(policier.getGender());
        existing.setBirthDate(policier.getBirthDate());
        existing.setLieu(policier.getLieu());
        existing.setTelephone(policier.getTelephone());

        // ajoute les autres champs si nécessaire

        return repository.save(existing);
    }

    // DELETE
    public void delete(Long id) {

        Policier policier = findById(id);

        repository.delete(policier);
    }

    public PolicierDto findByMatricule(String matricule) {

        Policier p = repository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Policier introuvable"));

        PolicierDto dto = new PolicierDto();

        dto.setId(p.getId());
        dto.setMatricule(p.getMatricule());
        dto.setLastname(p.getLastname());
        dto.setPostname(p.getPostname());
        dto.setFirstnames(p.getFirstnames());
        dto.setBirthDate(p.getBirthDate());
        dto.setGender(p.getGender());
        dto.setCityBirth(p.getCityBirth());
        dto.setLieu(p.getLieu());
        dto.setCountryBirth(p.getCountryBirth());
        dto.setDateAdded(p.getDateAdded());
        dto.setRank(p.getRank());
        dto.setRankNominationActDate(p.getRankNominationActDate());
        dto.setDateEntryInPolice(p.getDateEntryInPolice());
        dto.setProfession(p.getProfession());
        dto.setProfessionStartDate(p.getProfessionStartDate());
        dto.setMainUnit(p.getMainUnit());
        dto.setUnit(p.getUnit());
        dto.setSpouseLastname(p.getSpouseLastname());
        dto.setSpousePostname(p.getSpousePostname());
        dto.setSpouseFirstname(p.getSpouseFirstname());
        dto.setSpouseNationality(p.getSpouseNationality());
        dto.setSpouseProfession(p.getSpouseProfession());
        dto.setBloodtype(p.getBloodtype());
        dto.setDistrictOrigin(p.getDistrictOrigin());
        dto.setTerritoireOrigin(p.getTerritoireOrigin());
        dto.setVillageOrigin(p.getVillageOrigin());
        dto.setAddressStreet(p.getAddressStreet());
        dto.setAddressCommune(p.getAddressCommune());
        dto.setTelephone(p.getTelephone());
        dto.setEmergencyLastname(p.getEmergencyLastname());
        dto.setEmergencyPostname(p.getEmergencyPostname());
        dto.setEmergencyFirstname(p.getEmergencyFirstname());
        dto.setEmergencyRelation(p.getEmergencyRelation());
        dto.setEmergencyAddressStreet(p.getEmergencyAddressStreet());
        dto.setEmergencyAddressCommune(p.getEmergencyAddressCommune());
        dto.setEmergencyTelephone(p.getEmergencyTelephone());
        dto.setPosition(p.getPosition());

        dto.setPkPhoto(p.getPkPhoto());

        // PHOTO URL
        if (p.getPkPhoto() != null && !p.getPkPhoto().isEmpty()) {

            dto.setPhotoUrl(
                    "photos/" + p.getPkPhoto() + ".jpg");
        }

        return dto;
    }

    public Policier findByIdentite(
            String lastname,
            String postname,
            String firstnames,
            LocalDate birthDate) {

        return repository
                .findByLastnameAndPostnameAndFirstnamesAndBirthDate(
                        lastname,
                        postname,
                        firstnames,
                        birthDate)
                .orElseThrow(() -> new RuntimeException("Policier introuvable"));
    }
}