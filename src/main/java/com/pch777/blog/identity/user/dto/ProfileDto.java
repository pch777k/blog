package com.pch777.blog.identity.user.dto;


import com.pch777.blog.identity.user.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private UUID userId;
    private String fullName;
    private String avatarUrl;
    private Role role;
    private int totalUnreadMessages;
    private int totalUnreadNotifications;

}
