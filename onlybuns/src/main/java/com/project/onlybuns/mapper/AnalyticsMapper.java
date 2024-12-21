package com.project.onlybuns.mapper;

import com.project.onlybuns.dto.AnalyticsDto;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsMapper {
    public AnalyticsDto toAnalyticsDto(
            long weeklyPosts, long monthlyPosts, long yearlyPosts,
            long weeklyComments, long monthlyComments, long yearlyComments,
            double usersWithPostsOnlyPercent, double usersWithCommentsOnlyPercent, double inactiveUsersPercent) {

        return AnalyticsDto.builder()
                .weeklyPosts(weeklyPosts)
                .monthlyPosts(monthlyPosts)
                .yearlyPosts(yearlyPosts)
                .weeklyComments(weeklyComments)
                .monthlyComments(monthlyComments)
                .yearlyComments(yearlyComments)
                .usersWithPostsOnly(usersWithPostsOnlyPercent)
                .usersWithCommentsOnly(usersWithCommentsOnlyPercent)
                .inactiveUsers(inactiveUsersPercent)
                .build();
    }

}
