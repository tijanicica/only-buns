package com.project.onlybuns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostsDto {
    private long weeklyPosts;
    private long monthlyPosts;
    private long yearlyPosts;
}

