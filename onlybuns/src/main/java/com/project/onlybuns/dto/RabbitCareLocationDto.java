package com.project.onlybuns.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RabbitCareLocationDto {
    private String id;
    private String name;
    private double latitude;
    private double longitude;

}

