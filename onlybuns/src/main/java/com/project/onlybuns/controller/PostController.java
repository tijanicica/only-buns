package com.project.onlybuns.controller;

import com.project.onlybuns.dto.CommentDto;
import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.model.Like;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CommentService commentService;

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

        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                System.out.println("Unauthorized access attempt: Missing or invalid Authorization header.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please register your account first.");
            }

            String token = authorization.split(" ")[1];
            String userEmail = jwtService.extractUsername(token);

            if (userEmail == null) {
                System.out.println("Unauthorized access attempt: Failed to extract username from token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please register your account first.");
            }

            likeService.likePost(postId, userEmail);
            return ResponseEntity.ok("Post liked successfully!");

        } catch (Exception e) {
            
            System.out.println("An error occurred while processing the like request: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/{postId}/likes")
    public List<Like> getLikesForPost(@PathVariable Integer postId) {
        return likeService.getLikesForPost(postId);
    }



    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> getPostDetails(@PathVariable Integer postId) {
        PostDto postDto = postService.getPostDtoById(postId);

        RegisteredUser creator = registeredUserService.findById(postDto.getCreatorId());
        String creatorEmail = creator.getEmail();

        List<CommentDto> comments = commentService.getCommentsForPost(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("post", postDto);
        response.put("comments", comments);
        response.put("creatorEmail", creatorEmail);
        response.put("likesCount", likeService.getLikesForPost(postId).size());
        response.put("commentsCount", comments.size());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDto> submitComment(@PathVariable Integer postId,
                                                    @RequestBody CommentDto commentDto,
                                                    @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                System.out.println("Unauthorized access attempt: Missing or invalid Authorization header.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Unauthorized response
            }

            String token = authorization.split(" ")[1];

            String userEmail = jwtService.extractUsername(token);

            if (userEmail == null) {
                System.out.println("Unauthorized access attempt: Failed to extract username from token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Unauthorized response
            }

            Post post = postService.getPostById(postId);
            if (post == null) {
                System.out.println("Post not found with ID: " + postId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            RegisteredUser user = registeredUserService.findByEmail(userEmail);
            if (user == null) {
                System.out.println("User not found with email: " + userEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }


            CommentDto savedComment = commentService.submitComment(post, commentDto.getContent(), user);

            return ResponseEntity.ok(savedComment);

        } catch (Exception e) {
            System.out.println("An error occurred while submitting the comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable int id, @RequestBody PostDto postDto) {
        PostDto updatedPost = postService.updatePost(id, postDto);
        return ResponseEntity.ok(updatedPost);
    }


    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable int id) {
        postService.deletePost(id);
    }

    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @RequestHeader("Authorization") String authorization) throws IOException {
        String token = authorization.split(" ")[1];
        String userEmail = jwtService.extractUsername(token);

        RegisteredUser user = registeredUserService.findByEmail(userEmail);

        PostDto createdPost = postService.createPost(user, postDto);
        System.out.println("Created Post: " + createdPost);

        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/followed")
    public ResponseEntity<List<PostDto>> getFollowedUsersPosts(@RequestHeader("Authorization") String token) {
        String userEmail = jwtService.extractUsername(token.split(" ")[1]);
        RegisteredUser user = registeredUserService.findByEmail(userEmail);
        List<PostDto> posts = postService.getPostsFromFollowedUsers(user);
        return ResponseEntity.ok(posts);
    }

}
