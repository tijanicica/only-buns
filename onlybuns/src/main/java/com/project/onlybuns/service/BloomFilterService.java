package com.project.onlybuns.service;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.RegisteredUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.nio.charset.StandardCharsets;

@Service
public class BloomFilterService {

    private BloomFilter<String> usernameBloomFilter;

    private final RegisteredUserRepository registeredUserRepository;

    public BloomFilterService(RegisteredUserRepository registeredUserRepository) {
        this.registeredUserRepository = registeredUserRepository;
    }

    @PostConstruct
    public void initializeBloomFilter() {

        usernameBloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),
                10000,
                0.01); // false positive prediction(zeljena verovatnoca)


        registeredUserRepository.findAll()
                .stream()
                .map(user -> ((RegisteredUser) user).getUsername())
                .forEach(usernameBloomFilter::put);
    }

    public boolean mightContain(String username) {
        return usernameBloomFilter.mightContain(username);
    }

    public void addUsername(String username) {
        usernameBloomFilter.put(username);
    }
}

