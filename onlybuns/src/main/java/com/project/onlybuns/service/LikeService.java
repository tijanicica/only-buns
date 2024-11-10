package com.project.onlybuns.service;

import com.project.onlybuns.model.Like;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.LikeRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private RegisteredUserService registeredUserService;

    public void likePost(Integer postId, String userEmail) {
        Post post = postService.getPostById(postId);

        RegisteredUser user = registeredUserService.findByEmail(userEmail);

        Like existingLike = likeRepository.findByPostAndUser(post, user);
        if (existingLike != null) {
            throw new RuntimeException("User has already liked this post");
        }

        Like newLike = new Like();
        newLike.setPost(post);
        newLike.setUser(user);
        newLike.setDate(LocalDateTime.now());
        likeRepository.save(newLike);
    }

    public List<Like> getLikesForPost(Integer postId) {
        List<Like> likes = likeRepository.findByPostId(postId);

        // Force the initialization of lazy-loaded relationships
        for (Like like : likes) {
            Hibernate.initialize(like.getPost()); // Initialize post field
            Hibernate.initialize(like.getUser()); // Initialize user field
        }

        return likes;
    }
}



