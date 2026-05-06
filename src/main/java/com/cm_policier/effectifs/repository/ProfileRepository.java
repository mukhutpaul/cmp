package com.cm_policier.effectifs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByName(String name);
}
