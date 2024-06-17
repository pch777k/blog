package com.pch777.blog.identity.user.service;

import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.ForbiddenException;
import com.pch777.blog.exception.IncorrectOldPasswordException;
import com.pch777.blog.exception.UnauthorizedException;
import com.pch777.blog.exception.UserNotFoundException;
import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.dto.UserRegisterDto;
import com.pch777.blog.identity.user.dto.UserCreateByAdminDto;
import com.pch777.blog.security.UserPrincipal;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.repository.UserRepository;
import com.pch777.blog.identity.user.dto.ChangePasswordDto;
import com.pch777.blog.identity.permission.service.DefaultPermissions;
import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.permission.domain.repository.PermissionRepository;
import com.pch777.blog.identity.permission.domain.model.PermissionType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.pch777.blog.identity.user.domain.model.Role.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    private final PasswordEncoder passwordEncoder;
    private final BlogConfiguration blogConfiguration;
    private final ArticleRepository articleRepository;
    private final DefaultPermissions defaultPermissions;

    @Transactional
    public User registerUser(UserRegisterDto userRegisterDto) {
        return createUserByRole(userRegisterDto, Role.READER);
    }

    @Transactional
    public User createUser(UserCreateByAdminDto userCreateByAdminDto) {
        return createUserByRole(userCreateByAdminDto, userCreateByAdminDto.getRole());
    }

    private User createUserByRole(UserRegisterDto userRegisterDto, Role role) {
        Set<Permission> defaultPermissionsForRole = defaultPermissions.getDefaultPermissionsForRole(role);

        User user = switch (role) {
            case READER -> new Reader(userRegisterDto.getFirstName(),
                    userRegisterDto.getLastName(),
                    userRegisterDto.getUsername(),
                    passwordEncoder.encode(userRegisterDto.getPassword()),
                    userRegisterDto.getEmail(),
                    role);
            case AUTHOR, ADMIN -> new Author(userRegisterDto.getFirstName(),
                    userRegisterDto.getLastName(),
                    userRegisterDto.getUsername(),
                    passwordEncoder.encode(userRegisterDto.getPassword()),
                    userRegisterDto.getEmail(),
                    role);
        };

        user.setRole(role);
        user.setPermissions(defaultPermissionsForRole);
        user.setAvatarUrl(blogConfiguration.getDefaultAvatarUrl());
        user.setEnabled(true);

        return userRepository.save(user);
    }


    @Transactional
    public User addPermissionToUser(UUID userId, PermissionType permissionType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
//        if(!roleService.isPermissionAppropriateForRole(user.getRole().name(), permissionType)) {
//            throw new ForbiddenException("This permission is not available for this role");
//        }
        Permission permission = permissionRepository.findByPermissionType(permissionType)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found: " + permissionType));

        user.addPermission(permission);

        return userRepository.save(user);
    }

    @Transactional
    public User deletePermissionFromUser(UUID userId, PermissionType permissionType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        Permission permission = permissionRepository.findByPermissionType(permissionType)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found: " + permissionType));

        user.removePermission(permission);

        return userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getAdmins() {
        return userRepository.findByRole(ADMIN);
    }

    @Transactional(readOnly = true)
    public List<User> getAuthors() {
        return userRepository.findByRole(AUTHOR);
    }

    @Transactional(readOnly = true)
    public List<User> getReaders() {
        return userRepository.findByRole(READER);
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));
    }

    @Transactional
    public User updateUser(UUID id, UserRegisterDto userRegisterDto) {
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User", id));
        updatedUser.setFirstName(userRegisterDto.getFirstName());
        updatedUser.setLastName(userRegisterDto.getLastName());
        updatedUser.setUsername(userRegisterDto.getUsername());
        updatedUser.setPassword(userRegisterDto.getPassword());
        updatedUser.setEmail(userRegisterDto.getEmail());

        return userRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User changePassword(UUID userId, ChangePasswordDto changePasswordDto,
                               Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("You must be logged in to change your password");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        if (!userDetails.getUsername().equals(user.getUsername())) {
            throw new ForbiddenException("You are not authorized to change this user's password");
        }

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new IncorrectOldPasswordException("Incorrect old password");
        }

        String newPasswordEncoded = passwordEncoder.encode(changePasswordDto.getNewPassword());
        user.setPassword(newPasswordEncoded);
        return userRepository.save(user);
    }

    @Transactional
    public void updatePermissions(UUID userId, Set<UUID> permissions) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        Set<Permission> updatedPermissions = permissions.stream()
                .map(permissionRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        user.setPermissions(updatedPermissions);

    }

    @Transactional
    public void enableAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        user.setEnabled(true);
    }

    @Transactional
    public void disableAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        user.setEnabled(false);
    }
}
