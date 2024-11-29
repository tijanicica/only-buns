package com.project.onlybuns.controller;

import com.project.onlybuns.dto.*;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.service.IpTrackingService;
import com.project.onlybuns.service.PostService;
import com.project.onlybuns.service.RegisteredUserService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.juli.logging.Log;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.boot.Banner.Mode.LOG;

@RestController
@RequestMapping("/api/users")
public class RegisteredUserController {

    @Autowired
    private RegisteredUserService registeredUserService;
    @Autowired
    private PostService postService;
    private final IpTrackingService ipTrackingService;
    private final Logger LOG = LoggerFactory.getLogger(RegisteredUserController.class);

    public RegisteredUserController(IpTrackingService ipTrackingService) {
        this.ipTrackingService = ipTrackingService;
    }

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
    @RateLimiter(name = "login", fallbackMethod = "loginFallback")
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(HttpServletRequest request, @RequestBody LoginDto loginDto) {
        if (ipTrackingService.isBlocked(request)) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }


        ipTrackingService.trackAttempt(request);
        String token = registeredUserService.login(loginDto);
        if (!token.isEmpty()) {
            return ResponseEntity.ok(new TokenDto(token));
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }
    public ResponseEntity<String> loginFallback(LoginDto loginRequest, RequestNotPermitted ex) {
        LOG.warn("Too many logins. Try again later");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Too many logins. Try again later");
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

    @PutMapping("/edit-profile")
    public ResponseEntity<?> editProfile(@RequestBody EditUserDto userEditDTO, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            registeredUserService.updateUserProfile(userDetails.getUsername(), userEditDTO);
            return ResponseEntity.ok("Profile updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }




}
