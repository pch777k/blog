package com.pch777.blog.identity.user.domain.model;

import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.permission.domain.model.PermissionType;
import com.pch777.blog.notification.domain.model.Notification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique = true, length = 50, nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true, length = 100, nullable = false)
    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private LocalDateTime created;

    private LocalDateTime modified;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @Formula("(SELECT COUNT(*) FROM comments s WHERE s.user_id = id)")
    private int commentCount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    protected User(String firstName, String lastName, String username, String password, String email, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @PrePersist
    void prePersist() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.modified = LocalDateTime.now();
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(PermissionType permission) {
        return permissions.stream().anyMatch(p -> p.getPermissionType().equals(permission));
    }
}
