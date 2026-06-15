package com.cm_policier.effectifs.service;



import java.util.List;

import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.dto.PersonRequest;
import com.cm_policier.effectifs.model.Person;
import com.cm_policier.effectifs.repository.PersonRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PersonService {

    
    private final PersonRepository personRepository;

    // =========================
    // CREATE
    // =========================
    @Transactional
    public Person create(PersonRequest request) {

        Person remplaceMil = null;

        if (request.getRemplaceMilUuid() != null) {

            remplaceMil = personRepository.findById(
                    request.getRemplaceMilUuid()
            ).orElseThrow(() ->
                    new RuntimeException("Person remplaceMil not found"));
        }

        Person person = Person.builder()
                .uuid(request.getUuid())
                .name(request.getName())
                .firstname(request.getFirstname())
                .postname(request.getPostname())
                .sex(request.getSex())
                .grade(request.getGrade())
                .unit(request.getUnit())
                .battalion(request.getBattalion())
                .birthdate(request.getBirthdate())
                .nominationdate(request.getNominationdate())
                .province(request.getProvince())
                .district(request.getDistrict())
                .status(request.getStatus())
                .idPersonnel(request.getIdPersonnel())
                .remplaceMil(remplaceMil)
                .build();

        return personRepository.save(person);
    }

    // =========================
    // GET ALL
    // =========================
    public List<Person> getAll() {
        return personRepository.findAll();
    }

    // =========================
    // GET BY ID
    // =========================
    public Person getById(String uuid) {
        return personRepository.findById(uuid)
                .orElseThrow(() ->
                        new RuntimeException("Person not found"));
    }

    // =========================
    // UPDATE
    // =========================
    @Transactional
    public Person update(String uuid, PersonRequest request) {

        Person existing = personRepository.findById(uuid)
                .orElseThrow(() ->
                        new RuntimeException("Person not found"));

        existing.setName(request.getName());
        existing.setFirstname(request.getFirstname());
        existing.setPostname(request.getPostname());
        existing.setSex(request.getSex());
        existing.setGrade(request.getGrade());
        existing.setUnit(request.getUnit());
        existing.setBattalion(request.getBattalion());
        existing.setBirthdate(request.getBirthdate());
        existing.setNominationdate(request.getNominationdate());
        existing.setProvince(request.getProvince());
        existing.setDistrict(request.getDistrict());
        existing.setStatus(request.getStatus());
        existing.setIdPersonnel(request.getIdPersonnel());

        // remplaceMil
        if (request.getRemplaceMilUuid() != null) {

            Person remplaceMil = personRepository.findById(
                    request.getRemplaceMilUuid()
            ).orElseThrow(() ->
                    new RuntimeException("RemplaceMil not found"));

            existing.setRemplaceMil(remplaceMil);

        } else {

            existing.setRemplaceMil(null);
        }

        return personRepository.save(existing);
    }

    // =========================
    // DELETE
    // =========================
    public void delete(String uuid) {

        Person person = personRepository.findById(uuid)
                .orElseThrow(() ->
                        new RuntimeException("Person not found"));

        personRepository.delete(person);
    }
}