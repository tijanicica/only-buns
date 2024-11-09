package com.project.onlybuns.mapper;

import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.model.Location;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    public PostDto toPostDto(Post post) {
        return PostDto.builder().id(post.getId()).photo(post.getPhoto()).description(post.getDescription()).createdAt(post.getCreatedAt())
                        .creatorId(post.getPostCreator().getId()).creatorUsername(post.getPostCreator().getFirstName()).locationId(post.getLocation().getId()).build();

    }

    public Post toPost(PostDto postDto, RegisteredUser user, Location location) {
        return Post.builder().id(postDto.getId()).postCreator(user).location(location).photo(postDto.getPhoto()).createdAt(postDto.getCreatedAt()
                ).description(postDto.getDescription()).build();
    }
}
