package com.project.onlybuns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentsDto {
    private long weeklyComments;
    private long monthlyComments;
    private long yearlyComments;
}
