package com.project.onlybuns.dto;

import com.project.onlybuns.model.Location;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PostDto {

    private int id;
    private String description;
    private String photo;
    private LocalDateTime createdAt;
    private Integer creatorId;
    private String creatorUsername;
    private Integer locationId;
    private Location location;
}
