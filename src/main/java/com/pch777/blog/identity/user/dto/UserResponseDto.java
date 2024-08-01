package com.pch777.blog.identity.user.dto;

import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.domain.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class UserResponseDto {

    private UUID id;
    private String username;
    private String email;
    private Role role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
