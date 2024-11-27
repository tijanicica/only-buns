package com.project.onlybuns.mapper;

import com.project.onlybuns.dto.PostDto;
import com.project.onlybuns.dto.RegisteredUserDto;
import com.project.onlybuns.model.Location;
import com.project.onlybuns.model.Post;
import com.project.onlybuns.model.RegisteredUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisteredUserMapper {


    public RegisteredUserDto toUserDto(RegisteredUser registeredUser) {
        Location address = registeredUser.getAddress();

        return RegisteredUserDto.builder()
                .id(registeredUser.getId())
                .firstName(registeredUser.getFirstName())
                .lastName(registeredUser.getLastName())
                .username(registeredUser.getUsername())
                .email(registeredUser.getEmail())
                .followersNumber(registeredUser.getFollowersNumber())
                .streetNumber(address != null ? address.getStreetNumber() : null)
                .streetName(address != null ? address.getStreetName() : null)
                .city(address != null ? address.getCity() : null)
                .country(address != null ? address.getCountry() : null)
                .build();

    }

}
