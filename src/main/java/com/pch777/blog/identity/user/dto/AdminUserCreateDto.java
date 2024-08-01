package com.pch777.blog.identity.user.dto;

import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.validation.PasswordMatches;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@PasswordMatches
public class AdminUserCreateDto extends UserRegisterDto {

    @NotNull(message = "must not be null")
    private Role role;
}
