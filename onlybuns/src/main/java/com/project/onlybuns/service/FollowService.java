package com.project.onlybuns.service;

import com.project.onlybuns.model.Follow;
import com.project.onlybuns.repository.FollowRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@AllArgsConstructor
@Service
public class FollowService
{

    private final FollowRepository followRepository;

    List<Follow> findByFollowedUserEmail(String email){
        return followRepository.findByFollowedUserEmail(email);
    }

    List<Follow> findByFollowerEmail(String email){
        return followRepository.findByFollowedUserEmail(email);
    }
}
