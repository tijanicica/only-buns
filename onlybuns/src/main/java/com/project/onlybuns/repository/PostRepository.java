package com.project.onlybuns.repository;

import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findAllByPostCreator(RegisteredUser postCreator);

    int countByPostCreatorId(Integer userId);

    List<Post> findAllByPostCreatorIn(List<RegisteredUser> users);



}
