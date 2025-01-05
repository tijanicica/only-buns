package com.project.onlybuns.controller;

import com.project.onlybuns.dto.AnalyticsDto;
import com.project.onlybuns.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/posts-comments")
    public AnalyticsDto getPostsAndCommentsAnalytics() {
        AnalyticsDto analytics = analyticsService.getAnalytics();
        return analytics;
    }
}
