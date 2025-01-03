package com.project.onlybuns.controller;

import com.project.onlybuns.model.RabbitCareLocation;
import com.project.onlybuns.repository.RabbitCareLocationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rabbit-care")
public class RabbitCareLocationController {
    private final RabbitCareLocationRepository repository;

    public RabbitCareLocationController(RabbitCareLocationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/locations")
    public List<RabbitCareLocation> getAllLocations() {
        return repository.findAll();
    }
}
