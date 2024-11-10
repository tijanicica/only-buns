package com.project.onlybuns.mapper;

import com.project.onlybuns.dto.CommentDto;
import com.project.onlybuns.model.Comment;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .date(comment.getDate())
                .username(comment.getUser().getUsername())
                .name(comment.getUser().getFirstName())
                .postId(comment.getPost().getId())
                .build();
    }

    public Comment toComment(CommentDto commentDto, RegisteredUser user, Post post) {
        return Comment.builder()
                .content(commentDto.getContent())
                .date(commentDto.getDate())
                .user(user)
                .post(post)
                .build();
    }
}
