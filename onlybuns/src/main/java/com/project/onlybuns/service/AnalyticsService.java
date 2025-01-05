package com.project.onlybuns.service;

import com.project.onlybuns.dto.AnalyticsDto;
import com.project.onlybuns.dto.CommentsDto;
import com.project.onlybuns.dto.PostsDto;
import com.project.onlybuns.dto.UserActivityDto;
import com.project.onlybuns.mapper.AnalyticsMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class AnalyticsService {

    private final PostService postService;
    private final CommentService commentService;
    private final RegisteredUserService userService;
    private final AnalyticsMapper analyticsMapper;

    @Autowired
    public AnalyticsService(PostService postService, CommentService commentService, RegisteredUserService userService, AnalyticsMapper analyticsMapper) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.analyticsMapper = analyticsMapper;
    }

    public AnalyticsDto getAnalytics() {
        // Broj objava
        long weeklyPosts = postService.countByCreatedAtAfter(getDateFor("WEEK"));

        long monthlyPosts = postService.countByCreatedAtAfter(getDateFor("MONTH"));
        long yearlyPosts = postService.countByCreatedAtAfter(getDateFor("YEAR"));

        // Broj komentara
        long weeklyComments = commentService.countByCreatedAtAfter(getDateFor("WEEK"));
        long monthlyComments = commentService.countByCreatedAtAfter(getDateFor("MONTH"));
        long yearlyComments = commentService.countByCreatedAtAfter(getDateFor("YEAR"));

        // Aktivnost korisnika
        // Izračunavanje korisnika
        long totalUsers = userService.countUsers();
        long usersWithBoth = postService.countUsersWithPostsAndComments();
        long usersWithPostsOnly = postService.countUsersWithPosts() - usersWithBoth;
        long usersWithCommentsOnly = commentService.countUsersWithComments() - usersWithBoth;
        long inactiveUsers = totalUsers - (usersWithPostsOnly + usersWithCommentsOnly + usersWithBoth);

// Procenti
        double usersWithPostsOnlyPercent = (usersWithPostsOnly / (double) totalUsers) * 100;
        double usersWithCommentsOnlyPercent = (usersWithCommentsOnly / (double) totalUsers) * 100;
        double inactiveUsersPercent = (inactiveUsers / (double) totalUsers) * 100;


        // Kreiraj DTO koristeći mapper
        return analyticsMapper.toAnalyticsDto(
                weeklyPosts, monthlyPosts, yearlyPosts,
                weeklyComments, monthlyComments, yearlyComments,
                usersWithPostsOnlyPercent, usersWithCommentsOnlyPercent, inactiveUsersPercent
        );
    }




    private LocalDateTime getDateFor(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case "WEEK":
                return now.minusWeeks(1);
            case "MONTH":
                return now.minusMonths(1);
            case "YEAR":
                return now.minusYears(1);
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }
    }
}

