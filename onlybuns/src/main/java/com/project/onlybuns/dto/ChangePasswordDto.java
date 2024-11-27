package com.project.onlybuns.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChangePasswordDto {

    private String email;
    private String oldPassword;
    private String newPassword;

}
