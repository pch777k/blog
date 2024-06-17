package com.pch777.blog.identity.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDisplayDto {
    private String action;
    private PermissionPossesDto articlePermission;
    private PermissionPossesDto commentPermission;
    private PermissionPossesDto messagePermission;
    private PermissionPossesDto userPermission;
}
