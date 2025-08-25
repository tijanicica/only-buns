package com.project.onlybuns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserActivityDto {
    private double usersWithPosts;
    private double usersWithCommentsOnly;
    private double inactiveUsers;
}

