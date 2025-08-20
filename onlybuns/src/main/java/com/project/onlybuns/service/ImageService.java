// src/main/java/com/project/onlybuns/service/ImageService.java
package com.project.onlybuns.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    @Cacheable(value = "imageCache", key = "#imageName")
    public byte[] getImageBytes(String imageName) throws IOException {
        System.out.println("!!! ČITANJE SLIKE SA DISKA: " + imageName + " !!!");
        String workingDirectory = System.getProperty("user.dir");
        Path imagePath = Paths.get(workingDirectory, "..", "only-buns-frontend", "public", "images", imageName);

        if (!Files.exists(imagePath)) {
            return null;
        }
        return Files.readAllBytes(imagePath);
    }

    @CacheEvict(value = "imageCache", key = "#imageName")
    public void evictImageFromCache(String imageName) {
        System.out.println("Brisanje slike iz keša: " + imageName);
    }
}