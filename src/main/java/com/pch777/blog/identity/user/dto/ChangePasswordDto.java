package com.pch777.blog.identity.user.dto;

import com.pch777.blog.validation.PasswordMatches;
import com.pch777.blog.validation.ValidOldPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@PasswordMatches
@ToString
public class ChangePasswordDto {

    @ValidOldPassword
    String oldPassword;

    @NotBlank(message = "must not be blank")
    @Size(message = "size must be between {min} and {max}", min = 3, max = 50)
    String newPassword;

    String confirmPassword;
}
