package com.project.onlybuns.service;

import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.mapper.PostMapper;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public List<PostDto> getAllSortedByDate() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(this.postMapper::toPostDto).toList();
    }

}
