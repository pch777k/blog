package com.pch777.blog.identity.user.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.AdminUserCreateDto;
import com.pch777.blog.identity.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserApiController {

    private final UserService userService;
    private final BlogConfiguration blogConfiguration;

    @GetMapping("/current-user")
    public UserDetails getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("authors")
    public List<User> getAuthors() {
        return userService.getAuthors();
    }

    @GetMapping("admins")
    public List<User> getAdmins() {
        return userService.getAdmins();
    }

    @GetMapping("readers")
    public List<User> getReaders() {
        return userService.getReaders();
    }


    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUserByAdmin(@Valid @RequestBody AdminUserCreateDto adminUserCreateDto) {
        return userService.createUser(adminUserCreateDto, blogConfiguration.getVerificationApiBaseUrl());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{userId}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable UUID userId) {
        userService.enableAccount(userId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{userId}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable UUID userId) {
        userService.disableAccount(userId);
        return ResponseEntity.accepted().build();
    }

}
