package com.pch777.blog.identity.permission.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PermissionType permissionType;

//    @ManyToMany(mappedBy = "permissions")
//    @JsonIgnoreProperties("permissions")
//    private Set<Role> roles = new HashSet<>();

    public Permission(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

//    public void addRole(Role role) {
//        roles.add(role);
//        role.getPermissions().add(this);
//    }
//
//    public void removeRole(Role role) {
//        roles.remove(role);
//        role.getPermissions().remove(this);
//    }
//
//    public void removeRoles() {
//        Permission self = this;
//        roles.forEach(role -> role.getPermissions().remove(self));
//        roles.clear();
//    }
}

