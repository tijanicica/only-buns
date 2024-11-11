package com.project.onlybuns.controller;

import com.project.onlybuns.dto.*;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.service.PostService;
import com.project.onlybuns.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class RegisteredUserController {

    @Autowired
    private RegisteredUserService registeredUserService;
    @Autowired
    private PostService postService;

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


}
