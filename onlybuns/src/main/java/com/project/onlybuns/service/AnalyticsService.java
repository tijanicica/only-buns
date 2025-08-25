package com.project.onlybuns.service;

import com.project.onlybuns.dto.AnalyticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
public class AnalyticsService {

    private final PostService postService;
    private final CommentService commentService;
    private final RegisteredUserService userService;

    @Autowired
    public AnalyticsService(PostService postService, CommentService commentService, RegisteredUserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    public AnalyticsDto getAnalytics(Integer year, Integer month, Integer week) {
        // --- Kod za određivanje početnog i krajnjeg datuma ---
        LocalDateTime start;
        LocalDateTime end;

        if (week != null && month != null) {
            // Logika za specifičnu nedelju u mesecu
            LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
            // Pomeramo se na početak izabrane nedelje (uvek Ponedeljak)
            LocalDate startOfWeek = firstDayOfMonth.plusWeeks(week - 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            start = startOfWeek.atStartOfDay();
            end = start.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        } else if (month != null) {
            // Logika za ceo mesec
            start = LocalDateTime.of(year, month, 1, 0, 0);
            end = start.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        } else {
            // Logika za celu godinu
            start = LocalDateTime.of(year, 1, 1, 0, 0);
            end = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        }
        // --- Kraj koda za datume ---


        // 1. Broj postova i komentara u periodu
        long postsInPeriod = postService.countPostsBetween(start, end);
        long commentsInPeriod = commentService.countCommentsBetween(start, end);

        // 2. Aktivnost korisnika U PERIODU
        long totalUsers = userService.countUsers();
        if (totalUsers == 0) {
            return new AnalyticsDto(postsInPeriod, commentsInPeriod, 0, 0, 100);
        }

        long usersWithPostsInPeriod = postService.countUsersWithPostsBetween(start, end);
        long usersWithCommentsInPeriod = commentService.countUsersWithCommentsBetween(start, end);
        long usersWithBothInPeriod = postService.countUsersWithPostsAndCommentsBetween(start, end);

        long usersWithPostsOnlyInPeriod = usersWithPostsInPeriod - usersWithBothInPeriod;
        long usersWithCommentsOnlyInPeriod = usersWithCommentsInPeriod - usersWithBothInPeriod;

        long activeUsersInPeriod = usersWithPostsInPeriod + usersWithCommentsOnlyInPeriod;
        long inactiveUsersInPeriod = totalUsers - activeUsersInPeriod;

        // Procenti
        double usersWithPostsPercent = (usersWithPostsInPeriod / (double) totalUsers) * 100;
        double usersWithCommentsOnlyPercent = (usersWithCommentsOnlyInPeriod / (double) totalUsers) * 100;
        double inactiveUsersPercent = (inactiveUsersInPeriod / (double) totalUsers) * 100;

        return new AnalyticsDto(
                postsInPeriod,
                commentsInPeriod,
                usersWithPostsPercent,
                usersWithCommentsOnlyPercent,
                inactiveUsersPercent
        );
    }
}