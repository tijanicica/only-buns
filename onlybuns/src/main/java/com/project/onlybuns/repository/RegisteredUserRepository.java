package com.project.onlybuns.repository;

import com.project.onlybuns.model.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Integer> {

    Optional<RegisteredUser> findByEmail(String email);

    Optional<RegisteredUser> findByUsername(String username);
    Optional<RegisteredUser> findByActivationToken(String token);

    @Query("SELECT u FROM RegisteredUser u " +
            "WHERE (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
            "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:minPosts IS NULL OR (SELECT COUNT(p) FROM Post p WHERE p.postCreator.id = u.id) >= :minPosts) " +
            "AND (:maxPosts IS NULL OR (SELECT COUNT(p) FROM Post p WHERE p.postCreator.id = u.id) <= :maxPosts) ")
    Page<RegisteredUser> searchUsers(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("minPosts") Integer minPosts,
            @Param("maxPosts") Integer maxPosts,
            Pageable pageable);






}


