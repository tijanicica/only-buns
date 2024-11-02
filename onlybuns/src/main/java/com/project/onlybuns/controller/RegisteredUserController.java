package com.project.onlybuns.controller;

import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.dto.RegisteredUserDto;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.service.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class RegisteredUserController {

    @Autowired
    private RegisteredUserService registeredUserService;

    // Endpoint za prikaz profila korisnika
    @GetMapping("/{userId}")
    public ResponseEntity<RegisteredUserDto> getUserProfile(@PathVariable Integer userId) {
        RegisteredUserDto user = registeredUserService.getUserProfile(userId);
        return ResponseEntity.ok(user);
    }


}
