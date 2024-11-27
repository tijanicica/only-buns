package com.project.onlybuns.repository;

import com.project.onlybuns.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    List<Follow> findByFollowedUserEmail(String email);
    List<Follow> findByfollowerEmail(String email);
}
