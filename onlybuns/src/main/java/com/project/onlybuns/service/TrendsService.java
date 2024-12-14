package com.project.onlybuns.service;


import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.dto.RegisteredUserDto;
import com.project.onlybuns.dto.TrendsDto;
import com.project.onlybuns.mapper.PostMapper;
import com.project.onlybuns.mapper.RegisteredUserMapper;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.PostRepository;
import com.project.onlybuns.repository.RegisteredUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class TrendsService {

    private final PostRepository postRepository;
    private final RegisteredUserRepository registeredUserRepository;

    private final PostMapper postMapper;
    private final RegisteredUserMapper registeredUserMapper;

    public TrendsDto calculateTrendsStats() {
        long totalPosts = postRepository.countTotalPosts();
        long postsLastMonth = postRepository.countPostsInLastMonth(LocalDateTime.now().minusMonths(1));
        return new TrendsDto(totalPosts, postsLastMonth);
    }

    public List<PostDto> getPopularPostsLastWeek() {
        return postRepository.findPopularPostsInLastWeek(LocalDateTime.now().minusDays(7))
                .stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public List<PostDto> getAllTimePopularPosts() {
        return postRepository.findAllTimePopularPosts()
                .stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public List<RegisteredUserDto> getTopLikersLastWeek() {
        return registeredUserRepository.findTopLikersInLastWeek(LocalDateTime.now().minusDays(7))
                .stream()
                .map(registeredUserMapper::toUserDto)
                .toList();
    }
}
