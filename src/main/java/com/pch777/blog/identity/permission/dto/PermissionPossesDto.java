package com.pch777.blog.identity.permission.dto;

import com.pch777.blog.identity.permission.domain.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionPossesDto {

    private Permission permission;
    private boolean hasPermission;
}
