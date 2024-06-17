package com.pch777.blog.identity.user.controller;

import com.pch777.blog.exception.ForbiddenException;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.permission.dto.PermissionDto;
import com.pch777.blog.identity.user.dto.UserCreateByAdminDto;
import com.pch777.blog.identity.user.dto.UserRegisterDto;
import com.pch777.blog.identity.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        return userService.registerUser(userRegisterDto);
    }

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUserByAdmin(@Valid @RequestBody UserCreateByAdminDto userCreateByAdminDto) {
        return userService.createUser(userCreateByAdminDto);
    }

//    @PutMapping("{id}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public User updateUser(@PathVariable UUID id,
//                           @Valid @RequestBody UserRegisterDto userRegisterDto) {
//        return userService.updateUser(id, userRegisterDto);
//    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{userId}/permissions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> addPermissionToUser(@PathVariable UUID userId,
                                                      @RequestBody PermissionDto permissionDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access.");
        }
        try {
            User user = userService.addPermissionToUser(userId, permissionDto.getPermissionType());
            return ResponseEntity.ok(user);
        } catch (ForbiddenException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{userId}/permissions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deletePermissionFromUser(@PathVariable UUID userId,
                                                      @RequestBody PermissionDto permissionDto,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access.");
        }

        try {
            User user = userService.deletePermissionFromUser(userId, permissionDto.getPermissionType());
            return ResponseEntity.ok(user);
        } catch (ForbiddenException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
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
