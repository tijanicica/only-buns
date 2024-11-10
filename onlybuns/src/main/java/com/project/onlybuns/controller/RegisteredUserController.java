package com.project.onlybuns.controller;

import com.project.onlybuns.dto.*;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class RegisteredUserController {

    @Autowired
    private RegisteredUserService registeredUserService;

    // Endpoint za prikaz profila korisnika
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
}
