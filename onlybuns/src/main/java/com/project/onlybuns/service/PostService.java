package com.project.onlybuns.service;

import com.project.onlybuns.dto.CommentDto;
import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.dto.RegisteredUserDto;
import com.project.onlybuns.mapper.CommentMapper;
import com.project.onlybuns.mapper.PostMapper;
import com.project.onlybuns.model.Comment;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final RegisteredUserService registeredUserService;
    private final CommentMapper commentMapper;
    private final CommentService commentService;

    public List<PostDto> getAllSortedByDate() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(this.postMapper::toPostDto).toList();
    }

    public List<PostDto> getUserPosts(Integer userId) {
        RegisteredUser user = registeredUserService.findById(userId);
        return postRepository.findAllByPostCreator(user).stream()
                .map(postMapper::toPostDto)
                .toList();
    }


    public Post getPostById(Integer postId) {
        return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public PostDto getPostDtoById(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postMapper.toPostDto(post);
    }

    public List<CommentDto> getCommentsForPost(Integer postId) {
        return commentService.getCommentsForPost(postId);
    }

    public CommentDto submitComment(Integer postId, String content, RegisteredUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return commentService.submitComment(post, content, user);
    }

    public PostDto updatePost(int id, PostDto postDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setDescription(postDto.getDescription());

        postRepository.save(post);

        return postMapper.toPostDto(post);
    }


    public void deletePost(int id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }

    public int countByUserId(Integer userId) {
        return postRepository.countByPostCreatorId(userId);
    }





}
