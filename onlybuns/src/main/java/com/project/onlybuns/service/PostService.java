package com.project.onlybuns.service;

import com.project.onlybuns.dto.CommentDto;
import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.mapper.CommentMapper;
import com.project.onlybuns.mapper.PostMapper;
import com.project.onlybuns.model.Location;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final RegisteredUserService registeredUserService;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final FollowService followService;

    public List<PostDto> getAllSortedByDate() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(this.postMapper::toPostDto).toList();
    }

    public List<PostDto> getUserPosts(Integer userId) {
        RegisteredUser user = registeredUserService.findById(userId);
        return postRepository.findAllByPostCreator(user).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public List<PostDto> getUserPostsByEmail(String email) {
        RegisteredUser user = registeredUserService.findByEmail(email);
        return postRepository.findAllByPostCreator(user).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    public PostDto createPost(RegisteredUser user, PostDto postDto) throws IOException {
        String photoUrl = savePhoto(postDto.getPhoto());
        Location location = new Location();
        location.setStreetNumber(postDto.getLocation().getStreetNumber());
        location.setStreetName(postDto.getLocation().getStreetName());
        location.setCity(postDto.getLocation().getCity());
        location.setCountry(postDto.getLocation().getCountry());

        Post post = new Post();
        post.setDescription(postDto.getDescription());
        post.setPostCreator(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setLocation(postDto.getLocation());
        post.setPhoto(photoUrl);

        postRepository.save(post);

        return postMapper.toPostDto(post);
    }
    public String savePhoto(String base64Photo) throws IOException {
            String workingDirectory = System.getProperty("user.dir");
            String uploadDirectory = Paths.get(workingDirectory, "..", "only-buns-frontend", "public", "images").toString();

            String fileName = "image_" + System.currentTimeMillis() + ".jpg";
            String base64Data = base64Photo.split(",")[1];

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            Path path = Paths.get(uploadDirectory, fileName);
            Files.write(path, imageBytes);

            return "/images/" + fileName;
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

    public List<PostDto> getPostsFromFollowedUsers(RegisteredUser user) {
        // Fetch the list of users the given user is following
        List<RegisteredUser> followedUsers = followService.findFollowedByUser(user.getId());

        // Retrieve and map posts from these followed users to PostDto
        return postRepository.findAllByPostCreatorIn(followedUsers).stream()
                .map(postMapper::toPostDto) // Use the provided mapper to convert to PostDto
                .toList(); // Convert the stream to a list
    }



}
