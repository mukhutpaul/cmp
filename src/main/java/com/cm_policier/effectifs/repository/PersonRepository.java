package com.cm_policier.effectifs.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Person;

public interface PersonRepository extends JpaRepository<Person, String> {

}
