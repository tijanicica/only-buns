package com.project.onlybuns.controller;

import com.project.onlybuns.dto.*;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.service.FollowService;
import com.project.onlybuns.service.JwtService;
import com.project.onlybuns.service.PostService;
import com.project.onlybuns.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class RegisteredUserController {

    @Autowired
    private RegisteredUserService registeredUserService;
    @Autowired
    private PostService postService;

    @Autowired
    private FollowService followService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/profile/{userId}")
    public ResponseEntity<RegisteredUserDto> getUserProfile(@PathVariable Integer userId) {
        RegisteredUserDto user = registeredUserService.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody RegistrationDto user) {
        registeredUserService.register(user);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        try {
            registeredUserService.activateUser(token);
            return ResponseEntity.ok("Account activated successfully!");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Activation link has expired.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        String token = registeredUserService.login(loginDto);
        if (!token.isEmpty()) {
            return ResponseEntity.ok(new TokenDto(token));
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }


    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean exists = registeredUserService.emailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        boolean exists = registeredUserService.usernameExists(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RegisteredUserDto>> getAllUsers() {
        List<RegisteredUserDto> users = registeredUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    //probaj sa dto ako stignes
    @GetMapping("/{userId}/postsCount")
    public ResponseEntity<Integer> getNumberOfPosts(@PathVariable Integer userId) {
        int postsCount = postService.countByUserId(userId);
        return ResponseEntity.ok(postsCount);
    }

    @GetMapping("/{userId}/followersCount")
    public ResponseEntity<Integer> getNumberOfFollowers(@PathVariable Integer userId) {
        int followersCount = registeredUserService.getNumberOfFollowers(userId);
        return ResponseEntity.ok(followersCount);
    }

    @PutMapping("/{userId}/makeAdmin")
    public ResponseEntity<Void> makeUserAdmin(@PathVariable Integer userId) {
        registeredUserService.makeUserAdmin(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RegisteredUserDto>> searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer minPosts,
            @RequestParam(required = false) Integer maxPosts,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "followersNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        Page<RegisteredUserDto> users = registeredUserService.searchUsers(
                firstName, lastName, email, minPosts, maxPosts, page, size, sortBy, order);

        return ResponseEntity.ok(users);
    }
    @GetMapping("/my-profile/{userEmail}")
    public ResponseEntity<RegisteredUserDto> getUserProfile(@PathVariable("userEmail") String userEmail) {
        RegisteredUserDto userProfile = registeredUserService.findDtoByEmail(userEmail);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/posts/{userEmail}")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable("userEmail") String userEmail) {
        List<PostDto> posts = postService.getUserPostsByEmail(userEmail);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/followers/{userEmail}")
    public ResponseEntity<List<RegisteredUserDto>> getFollowers(@PathVariable("userEmail") String userEmail) {
        List<RegisteredUserDto> followers = registeredUserService.getFollowers(userEmail);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following/{userEmail}")
    public ResponseEntity<List<RegisteredUserDto>> getFollowing(@PathVariable("userEmail") String userEmail) {
        List<RegisteredUserDto> following = registeredUserService.getFollowing(userEmail);
        return ResponseEntity.ok(following);
    }


    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordRequest) {
        try {
            registeredUserService.changePassword(changePasswordRequest.getEmail(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<String> followUser(
            @PathVariable Integer userId,
            @RequestHeader("Authorization") String authorization) {
        try {
            // Validate the Authorization header
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                System.out.println("Unauthorized access attempt: Missing or invalid Authorization header.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in to follow a user.");
            }

            // Extract the token and email
            String token = authorization.split(" ")[1];
            String userEmail = jwtService.extractUsername(token);

            if (userEmail == null) {
                System.out.println("Unauthorized access attempt: Failed to extract username from token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token. Please log in again.");
            }

            // Perform the follow operation
            registeredUserService.followUser(userId, userEmail);
            return ResponseEntity.ok("User followed successfully!");

        } catch (Exception e) {
            System.out.println("An error occurred while processing the follow request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }



    @GetMapping("/{userId}/followingCount")
    public ResponseEntity<Integer> getNumberOfFollowing(@PathVariable Integer userId) {
        int followingCount = followService.getNumberOfFollowing(userId);
        return ResponseEntity.ok(followingCount);
    }



    @GetMapping("/{userId}/isFollowing")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable Integer userId,
            @RequestHeader("Authorization") String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        String token = authorization.split(" ")[1];
        String userEmail = jwtService.extractUsername(token);
        RegisteredUser follower = registeredUserService.findByEmail(userEmail);
        RegisteredUser userToFollow = registeredUserService.findById(userId);

        boolean isFollowing = followService.isFollowing(userToFollow, follower);
        return ResponseEntity.ok(isFollowing);
    }

    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<String> unfollowUser(
            @PathVariable Integer userId,
            @RequestHeader("Authorization") String authorization) {
        try {
            // Validacija Authorization header-a
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in to unfollow a user.");
            }

            // Ekstrakcija tokena i email-a
            String token = authorization.split(" ")[1];
            String userEmail = jwtService.extractUsername(token);

            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token. Please log in again.");
            }

            // Prekid praćenja korisnika
            registeredUserService.unfollowUser(userId, userEmail);
            return ResponseEntity.ok("User unfollowed successfully!");

        } catch (Exception e) {
            System.out.println("An error occurred while processing the unfollow request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<RegisteredUserDto>> getFollowers(@PathVariable Integer userId, @RequestHeader("Authorization") String authorization) {
        System.out.println("Fetching followers for userId: " + userId);
        System.out.println("Authorization Header: " + authorization);

        List<RegisteredUserDto> followers = registeredUserService.getFollowersByUserId(userId);
        return ResponseEntity.ok(followers);
    }


    @GetMapping("/{userId}/following")
    public ResponseEntity<List<RegisteredUserDto>> getFollowing(@PathVariable Integer userId) {
        List<RegisteredUserDto> following = registeredUserService.getFollowingByUserId(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable Integer userId, @RequestHeader("Authorization") String authorization) {
        System.out.println("Authorization header: " + authorization);
        return ResponseEntity.ok(followService.getNumberOfFollowers(userId));
    }


    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Integer> getFollowingCount(@PathVariable Integer userId, @RequestHeader("Authorization") String authorization) {
        System.out.println("Authorization header: " + authorization);
        return ResponseEntity.ok(followService.getNumberOfFollowing(userId));
    }










}
