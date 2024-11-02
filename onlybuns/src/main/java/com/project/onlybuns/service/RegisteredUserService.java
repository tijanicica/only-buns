package com.project.onlybuns.service;

import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.dto.RegisteredUserDto;
import com.project.onlybuns.mapper.RegisteredUserMapper;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.RegisteredUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RegisteredUserService {


    private final RegisteredUserRepository registeredUserRepository;
    private final RegisteredUserMapper registeredUserMapper;

    public RegisteredUserDto getUserProfile(Integer userId) {
        return registeredUserRepository.findById(userId)
                .map(this.registeredUserMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public RegisteredUser findById(Integer userId) {
        return registeredUserRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }




}

