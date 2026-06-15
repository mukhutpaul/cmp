package com.cm_policier.effectifs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cm_policier.effectifs.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findAllByIds(@Param("ids") List<Long> ids);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findFullById(@Param("id") Long id);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<User> debugAll();

    List<User> findAllByOrderByIdDesc();

}