package com.project.onlybuns.service;

import com.project.onlybuns.model.Follow;
import com.project.onlybuns.model.RegisteredUser;
import com.project.onlybuns.repository.FollowRepository;
import com.project.onlybuns.repository.RegisteredUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final RegisteredUserRepository registeredUserRepository;

    public void followUser(RegisteredUser userToFollow, RegisteredUser follower) {
        if (userToFollow.getId().equals(follower.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        boolean alreadyFollowing = followRepository.existsByFollowedUserAndFollower(userToFollow, follower);
        if (alreadyFollowing) {
            throw new IllegalArgumentException("You are already following this user.");
        }

        Follow follow = new Follow();
        follow.setFollowedUser(userToFollow);
        follow.setFollower(follower);
        follow.setCreatedAt(LocalDateTime.now());

        followRepository.save(follow);

        // Update followers count
        userToFollow.setFollowersNumber(userToFollow.getFollowersNumber() + 1);
        registeredUserRepository.save(userToFollow);
    }

    public void unfollowUser(RegisteredUser userToUnfollow, RegisteredUser follower) {
        Follow follow = followRepository.findByFollowedUserAndFollower(userToUnfollow, follower)
                .orElseThrow(() -> new IllegalArgumentException("You are not following this user."));

        followRepository.delete(follow);

        // Update followers count
        userToUnfollow.setFollowersNumber(Math.max(userToUnfollow.getFollowersNumber() - 1, 0));
        registeredUserRepository.save(userToUnfollow);
    }

    public int getNumberOfFollowers(Integer userId) {
        return followRepository.countByFollowedUserId(userId);
    }

    public int getNumberOfFollowing(Integer userId) {
        return followRepository.countByFollowerId(userId);
    }


    public List<Follow> findByFollowedUserEmail(String email) {
        return followRepository.findByFollowedUserEmail(email);
    }

    public List<Follow> findByFollowerEmail(String email) {
        return followRepository.findByFollowerEmail(email);
    }

    public boolean isFollowing(RegisteredUser userToFollow, RegisteredUser follower) {
        return followRepository.existsByFollowedUserAndFollower(userToFollow, follower);
    }

    public List<RegisteredUser> findFollowedByUser(Integer userId) {
        List<Follow> followRelations = followRepository.findAllByFollowerId(userId);
        return followRelations.stream()
                .map(Follow::getFollowedUser)
                .collect(Collectors.toList());
    }

    public List<Follow> findFollowersByUser(RegisteredUser user) {
        return followRepository.findAllByFollowedUser(user);
    }

    public List<Follow> findFollowingByUser(RegisteredUser user) {
        return followRepository.findAllByFollower(user);
    }

    private final Map<String, List<Long>> userFollowTimestamps = new ConcurrentHashMap<>();

    public synchronized boolean canFollow(String userEmail) {
        long currentTime = System.currentTimeMillis();
        List<Long> timestamps = userFollowTimestamps.getOrDefault(userEmail, new ArrayList<>());

        // Uklanjanje praćenja starijih od jednog minuta
        timestamps.removeIf(timestamp -> (currentTime - timestamp) > 60_000);

        if (timestamps.size() < 50) {
            timestamps.add(currentTime);
            userFollowTimestamps.put(userEmail, timestamps);
            return true;
        }
        return false;
    }


}
