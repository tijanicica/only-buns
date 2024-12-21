package com.project.onlybuns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserActivityDto {
    private long usersWithPostsOnly;
    private long usersWithCommentsOnly;
    private long usersWithBoth;
    private long usersInactive;
}

