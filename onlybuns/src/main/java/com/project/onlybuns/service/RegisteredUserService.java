package com.project.onlybuns.service;

import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.RegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    public RegisteredUser getUserProfile(Integer userId) {
        return registeredUserRepository.findById(userId).orElse(null);
    }
}
