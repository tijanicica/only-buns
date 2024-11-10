package com.project.onlybuns.controller;

import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.model.Like;
import com.project.onlybuns.service.JwtService;
import com.project.onlybuns.service.LikeService;
import com.project.onlybuns.service.PostService;
import com.project.onlybuns.service.RegisteredUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private RegisteredUserService registeredUserService;

    @Autowired
    private JwtService jwtService;

    @GetMapping(value = "/all")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> posts =  postService.getAllSortedByDate();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable Integer userId) {
        List<PostDto> posts = postService.getUserPosts(userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Integer postId, @RequestHeader("Authorization") String authorization) {
        // Extract the JWT token from the Authorization header
        String token = authorization.split(" ")[1];
        String userEmail = jwtService.extractUsername(token); // This method will extract the username (email) from the JWT token

        likeService.likePost(postId, userEmail);
        return ResponseEntity.ok("Post liked successfully!");
    }

    @GetMapping("/{postId}/likes")
    public List<Like> getLikesForPost(@PathVariable Integer postId) {
        return likeService.getLikesForPost(postId);  // Return all likes for the specified post
    }




}
