package com.project.onlybuns.repository;

import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findAllByPostCreator(RegisteredUser postCreator);

    int countByPostCreatorId(Integer userId);


    @Query("SELECT COUNT(p) FROM Post p")
    long countTotalPosts();


    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt > :lastMonth")
    long countPostsInLastMonth(@Param("lastMonth") LocalDateTime lastMonth);


    @Query("SELECT p FROM Post p " +
            "LEFT JOIN Like l ON l.post = p " +
            "WHERE l.date > :lastWeek " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(l.id) DESC " +
            "LIMIT 5")
    List<Post> findPopularPostsInLastWeek(@Param("lastWeek") LocalDateTime lastWeek);


    @Query("SELECT p FROM Post p " +
            "LEFT JOIN Like l ON l.post = p " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(l.id) DESC " +
            "LIMIT 10")
    List<Post> findAllTimePopularPosts();



}
