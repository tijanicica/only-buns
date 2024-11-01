package com.project.onlybuns.service;

import com.project.onlybuns.model.Post;
import com.project.onlybuns.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllSortedByDate() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

}
