package com.project.onlybuns.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RegisteredUserDto {

    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private int followersNumber;
    private String streetNumber;
    private String streetName;
    private String city;
    private String country;

}
