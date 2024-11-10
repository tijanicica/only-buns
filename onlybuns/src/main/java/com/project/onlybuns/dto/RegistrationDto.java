package com.project.onlybuns.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RegistrationDto {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String username;
    private String streetName;
    private String city;
    private String country;
    private String streetNumber;
}
