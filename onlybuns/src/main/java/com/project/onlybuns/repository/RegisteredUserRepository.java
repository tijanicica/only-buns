package com.project.onlybuns.repository;

import com.project.onlybuns.model.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Integer> {

    Optional<RegisteredUser> findByEmail(String email);

    Optional<RegisteredUser> findByUsername(String username);
    Optional<RegisteredUser> findByActivationToken(String token);
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);


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

    @Query("SELECT u FROM RegisteredUser u " +
            "WHERE u.isActive = false " +
            "AND u.activationDate IS NULL " +
            "AND u.registrationDate <= :cutoffDate")
    List<RegisteredUser> findInactiveUsersBefore(@Param("cutoffDate") LocalDateTime cutoffDate);




    @Query("SELECT u FROM RegisteredUser u " +
            "LEFT JOIN Like l ON l.user = u " +
            "WHERE l.date > :lastWeek " +
            "GROUP BY u.id " +
            "ORDER BY COUNT(l.id) DESC " +
            "limit 10")
    List<RegisteredUser> findTopLikersInLastWeek(@Param("lastWeek") LocalDateTime lastWeek);


    long countByUsername(String user1);
}


