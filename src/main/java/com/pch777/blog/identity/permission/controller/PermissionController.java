package com.pch777.blog.identity.permission.controller;


import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.permission.domain.model.PermissionType;
import com.pch777.blog.identity.permission.domain.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/permissions")
public class PermissionController {

    private final PermissionRepository permissionRepository;

    @GetMapping
    List<Permission> getPermissions() {
        return permissionRepository.findAll();
    }

    @GetMapping("/{permissionType}")
    public ResponseEntity<Permission> getPermissionByType(@PathVariable PermissionType permissionType) {
        Optional<Permission> optionalPermission = permissionRepository.findByPermissionType(permissionType);

        return optionalPermission.map(permission -> ResponseEntity.ok().body(permission))
                .orElse(ResponseEntity.notFound().build());
    }
}
