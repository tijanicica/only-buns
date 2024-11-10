package com.project.onlybuns.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CommentDto {

    private int id;
    private String content;
    private LocalDateTime date;
    private String username;
    private String name;  //ime korisnika koji je dodao komentar
    private Integer postId;

}
