package com.pch777.blog.security;

import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.model.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private UUID id;
    private String username;
    private String email;
    private String password;
    private transient Role role;
    private transient Set<Permission> permissions;
    private boolean isEnabled;

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getPermissions(),
                user.isEnabled()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        permissions.forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getPermissionType().name())));

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username != null ? this.username : this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
