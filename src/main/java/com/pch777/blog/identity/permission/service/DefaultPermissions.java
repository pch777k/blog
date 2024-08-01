package com.pch777.blog.identity.permission.service;

import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.permission.domain.model.PermissionType;
import com.pch777.blog.identity.permission.dto.PermissionDisplayDto;
import com.pch777.blog.identity.permission.dto.PermissionPossesDto;
import com.pch777.blog.identity.user.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pch777.blog.identity.permission.domain.model.PermissionType.*;
import static com.pch777.blog.identity.permission.domain.model.PermissionType.USER_SUBSCRIBE;

@RequiredArgsConstructor
@Component
public class DefaultPermissions {

    private final PermissionService permissionService;

    public Set<Permission> getDefaultPermissionsForRole(Role role) {
        return switch (role) {
            case READER -> getReaderPermissions();
            case AUTHOR -> getAuthorPermissions();
            case ADMIN -> getAdminPermissions();
        };
    }

    private Set<Permission> getReaderPermissions() {
        return permissionService.findPermissionsByType(Set.of(
                ARTICLE_LIKE,
                COMMENT_UPDATE,
                COMMENT_CREATE,
                COMMENT_DELETE,
                MESSAGE_SEND,
                MESSAGE_RECEIVE,
                USER_UPDATE,
                USER_SUBSCRIBE

        ));
    }

    private Set<Permission> getAuthorPermissions() {
        return permissionService.findPermissionsByType(Set.of(
                ARTICLE_CREATE,
                ARTICLE_UPDATE,
                ARTICLE_DELETE,
                ARTICLE_LIKE,
                COMMENT_UPDATE,
                COMMENT_CREATE,
                COMMENT_DELETE,
                MESSAGE_SEND,
                MESSAGE_RECEIVE,
                USER_UPDATE

        ));
    }

    private Set<Permission> getAdminPermissions() {
        return permissionService.findPermissionsByType(Set.of(
                ARTICLE_CREATE,
                ARTICLE_UPDATE,
                ARTICLE_DELETE,
                ARTICLE_LIKE,
                COMMENT_UPDATE,
                COMMENT_CREATE,
                COMMENT_DELETE,
                MESSAGE_SEND,
                MESSAGE_RECEIVE,
                USER_CREATE,
                USER_UPDATE,
                USER_DELETE
        ));
    }

    public List<PermissionDisplayDto> getPermissionsForRole(Role role) {
        List<PermissionDisplayDto> permissions = new ArrayList<>();

        Set<PermissionType> permissionTypes = getDefaultPermissionsForRole(role)
                .stream()
                .map(Permission::getPermissionType)
                .collect(Collectors.toSet());

        permissions.add(createPermissionDisplayDto("CREATE", permissionTypes, ARTICLE_CREATE, COMMENT_CREATE, null, USER_CREATE));
        permissions.add(createPermissionDisplayDto("UPDATE", permissionTypes, ARTICLE_UPDATE, COMMENT_UPDATE, null, USER_UPDATE));
        permissions.add(createPermissionDisplayDto("DELETE", permissionTypes, ARTICLE_DELETE, COMMENT_DELETE, null, USER_DELETE));
        permissions.add(createPermissionDisplayDto("SEND", permissionTypes, null, null, MESSAGE_SEND, null));
        permissions.add(createPermissionDisplayDto("RECEIVE", permissionTypes, null, null, MESSAGE_RECEIVE, null));
        permissions.add(createPermissionDisplayDto("LIKE", permissionTypes, ARTICLE_LIKE, null, null, null));
        permissions.add(createPermissionDisplayDto("SUBSCRIBE", permissionTypes, null, null, null, USER_SUBSCRIBE));

        return permissions;
    }

    private PermissionDisplayDto createPermissionDisplayDto(String action, Set<PermissionType> permissionTypes,
                                                            PermissionType articleType, PermissionType commentType,
                                                            PermissionType messageType, PermissionType userType) {
        PermissionDisplayDto dto = new PermissionDisplayDto();
        dto.setAction(action);
        dto.setArticlePermission(createPermissionPossesDto(permissionTypes, articleType));
        dto.setCommentPermission(createPermissionPossesDto(permissionTypes, commentType));
        dto.setMessagePermission(createPermissionPossesDto(permissionTypes, messageType));
        dto.setUserPermission(createPermissionPossesDto(permissionTypes, userType));
        return dto;
    }

    private PermissionPossesDto createPermissionPossesDto(Set<PermissionType> permissionTypes, PermissionType type) {
        PermissionPossesDto possesDto = new PermissionPossesDto();
        if (type != null) {
            possesDto.setPermission(permissionService.findByPermissionType(type).orElse(null));
            possesDto.setHasPermission(permissionTypes.contains(type));
        }
        return possesDto;
    }
}
