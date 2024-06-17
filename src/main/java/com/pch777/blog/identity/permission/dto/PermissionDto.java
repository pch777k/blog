package com.pch777.blog.identity.permission.dto;

import com.pch777.blog.identity.permission.domain.model.PermissionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PermissionDto {

    private PermissionType permissionType;
}
