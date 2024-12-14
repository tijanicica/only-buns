package com.project.onlybuns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendsDto {
    private long totalPosts;
    private long postsLastMonth;
}
