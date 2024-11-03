package com.project.onlybuns.repository;

import com.project.onlybuns.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Integer> {

    Optional<RegisteredUser> findByEmail(String email);

    Optional<RegisteredUser> findByUsername(String username);
    Optional<RegisteredUser> findByActivationToken(String token);
}


