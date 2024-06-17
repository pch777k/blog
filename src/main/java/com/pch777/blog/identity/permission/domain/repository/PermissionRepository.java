package com.pch777.blog.identity.permission.domain.repository;

import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.permission.domain.model.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByPermissionType(PermissionType permissionType);


}
