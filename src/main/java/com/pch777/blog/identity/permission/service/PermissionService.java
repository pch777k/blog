package com.pch777.blog.identity.permission.service;

import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.permission.domain.model.PermissionType;
import com.pch777.blog.identity.permission.domain.repository.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public Set<Permission> findAllPermissions() {
        return new HashSet<>(permissionRepository.findAll());
    }

    public Optional<Permission> findByPermissionType(PermissionType permissionType) {
        return permissionRepository.findByPermissionType(permissionType);
    }

    public Set<Permission> findPermissionsByType(Set<PermissionType> types) {
        return types.stream()
                .map(permissionRepository::findByPermissionType)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }


    public Permission findById(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
    }
}





