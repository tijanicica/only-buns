package com.project.onlybuns.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.onlybuns.config.RabbitMqDirectConfig;
import com.project.onlybuns.dto.RabbitCareLocationDto;
import com.project.onlybuns.model.RabbitCareLocation;
import com.project.onlybuns.repository.RabbitCareLocationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitCareLocationService {
    private final RabbitCareLocationRepository repository;

    public RabbitCareLocationService(RabbitCareLocationRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMqDirectConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            RabbitCareLocationDto locationDTO = mapper.readValue(message, RabbitCareLocationDto.class);


            RabbitCareLocation location = new RabbitCareLocation();
            location.setLocationId(locationDTO.getId());
            location.setName(locationDTO.getName());
            location.setLatitude(locationDTO.getLatitude());
            location.setLongitude(locationDTO.getLongitude());

            repository.save(location);
            System.out.println("Location saved: " + location);
        } catch (Exception e) {
            System.err.println("Error during reading message: " + e.getMessage());
        }
    }
}

