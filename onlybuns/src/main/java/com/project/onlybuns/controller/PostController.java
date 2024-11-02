package com.project.onlybuns.controller;

import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping(value = "/all")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> posts =  postService.getAllSortedByDate();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable Integer userId) {
        List<PostDto> posts = postService.getUserPosts(userId);
        return ResponseEntity.ok(posts);
    }

}
