package com.project.onlybuns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class AnalyticsDto {
    private long posts;
    private long comments;
    private double usersWithPosts;
    private double usersWithCommentsOnly;
    private double inactiveUsers;


}
