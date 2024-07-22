package com.pch777.blog.identity.permission.dto;

import com.pch777.blog.identity.permission.domain.model.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionTypeSetDto {
    private Set<PermissionType> permissions;
}
