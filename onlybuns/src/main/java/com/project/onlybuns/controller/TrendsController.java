package com.project.onlybuns.controller;

import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.dto.RegisteredUserDto;
import com.project.onlybuns.dto.TrendsDto;
import com.project.onlybuns.service.TrendsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trends")
public class TrendsController {

    private final TrendsService trendsService;

    public TrendsController(TrendsService trendsService) {
        this.trendsService = trendsService;
    }

    @GetMapping("/stats")
    public TrendsDto getTrendsStats() {
        return trendsService.calculateTrendsStats();
    }

    @GetMapping("/popular-posts/weekly")
    public List<PostDto> getWeeklyPopularPosts() {
        return trendsService.getPopularPostsLastWeek();
    }

    @GetMapping("/popular-posts/all-time")
    public List<PostDto> getAllTimePopularPosts() {
        return trendsService.getAllTimePopularPosts();
    }

    @GetMapping("/top-likers/weekly")
    public List<RegisteredUserDto> getTopLikersLastWeek() {
        return trendsService.getTopLikersLastWeek();
    }
}

