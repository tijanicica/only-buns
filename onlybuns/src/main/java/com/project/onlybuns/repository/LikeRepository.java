package com.project.onlybuns.repository;

import com.project.onlybuns.model.Like;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Like findByPostAndUser(Post post, RegisteredUser user);
    List<Like> findByPostId(Integer postId);


}
