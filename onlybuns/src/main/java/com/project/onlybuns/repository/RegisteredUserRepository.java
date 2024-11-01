package com.project.onlybuns.repository;

import com.project.onlybuns.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Integer> {

}


