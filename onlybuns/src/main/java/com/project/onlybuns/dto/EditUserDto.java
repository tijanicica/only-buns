package com.project.onlybuns.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EditUserDto {

        private String firstName;
        private String lastName;
        private String streetName;
        private String streetNumber;
        private String city;
        private String country;
        private double longitude;
        private double latitude;



}
