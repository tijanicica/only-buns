package com.project.onlybuns.controller;

import com.project.onlybuns.dto.AnalyticsDto;
import com.project.onlybuns.dto.UserActivityDto;
import com.project.onlybuns.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/posts-comments")
    public AnalyticsDto getPostsAndCommentsAnalytics(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month, // Mesec je opcioni
            @RequestParam(required = false) Integer week   // Nedelja je opciona
    ) {
        return analyticsService.getAnalytics(year, month, week);
    }

    // Dodajemo endpoint za korisničku aktivnost, pošto ona nije vezana za datume
    /*@GetMapping("/user-activity")
    public UserActivityDto getUserActivityAnalytics() {
        return analyticsService.getUserActivity();
    }*/

    @GetMapping("/all")
    public AnalyticsDto getAllAnalytics(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week
    ) {
        return analyticsService.getAnalytics(year, month, week);
    }
}
