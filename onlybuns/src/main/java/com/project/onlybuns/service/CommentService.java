package com.project.onlybuns.service;

import com.project.onlybuns.dto.CommentDto;
import com.project.onlybuns.mapper.CommentMapper;
import com.project.onlybuns.model.Comment;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<CommentDto> getCommentsForPost(Integer postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(commentMapper::toCommentDto).toList();
    }

    public CommentDto submitComment(Post post, String content, RegisteredUser user) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        comment.setDate(java.time.LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        CommentDto commentDto = commentMapper.toCommentDto(savedComment);
        return commentDto;
    }


}
