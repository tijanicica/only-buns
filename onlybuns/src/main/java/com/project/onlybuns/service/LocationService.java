package com.project.onlybuns.service;

import com.project.onlybuns.model.Location;
import com.project.onlybuns.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public Location saveLocation(Location location) {
        // Persist the Location object and return the saved Location
        return locationRepository.save(location);
    }
}
