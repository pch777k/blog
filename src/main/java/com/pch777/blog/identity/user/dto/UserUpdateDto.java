package com.pch777.blog.identity.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDto {

    @NotBlank(message = "must not be blank")
    @Size(message = "size must be between {min} and {max}", min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "must not be blank")
    @Size(message = "size must be between {min} and {max}", min = 2, max = 50)
    private String lastName;

    private String avatarUrl;

    private String bio;
}
