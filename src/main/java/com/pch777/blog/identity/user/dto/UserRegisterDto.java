package com.pch777.blog.identity.user.dto;

import com.pch777.blog.validation.PasswordMatches;
import com.pch777.blog.validation.UniqueEmail;
import com.pch777.blog.validation.UniqueUsername;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@PasswordMatches
public class UserRegisterDto {

    @NotBlank(message = "must not be blank")
    @Size(message = "size must be between {min} and {max}", min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "must not be blank")
    @Size(message = "size must be between {min} and {max}", min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "must not be blank")
    @Size(message = "size must be between {min} and {max}", min = 3, max = 50)
    @Pattern(regexp = "\\S+", message = "cannot contain spaces")
    @UniqueUsername
    private String username;

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a well-formed email address")
    @Size(max = 100, message = "must be at most {max} characters long")
    @UniqueEmail
    private String email;

    @NotBlank(message = "must not be blank")
    @Size(message = "size must be between {min} and {max}", min = 3, max = 50)
    private String password;

    private String confirmPassword;

}
