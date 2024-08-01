package com.pch777.blog.identity.user.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.identity.admin.service.AdminService;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.author.service.AuthorService;
import com.pch777.blog.identity.permission.dto.PermissionSetDto;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.reader.service.ReaderService;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.AdminUserCreateDto;
import com.pch777.blog.identity.user.dto.AdminUserUpdateDto;
import com.pch777.blog.identity.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
@Slf4j
public class UserAdminApiController {


    private final UserService userService;
    private final ReaderService readerService;
    private final AuthorService authorService;
    private final AdminService adminService;
    private final BlogConfiguration blogConfiguration;

    @GetMapping("readers")
    public ResponseEntity<Page<Reader>> getReaders(
            @RequestParam(name = "s", required = false, defaultValue = "") String search,
            @RequestParam(name = "field", required = false, defaultValue = "id") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {

        int size = blogConfiguration.getPageSize();
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);
        Page<Reader> readersPage = readerService.getReaders(search, pageable);

        return ResponseEntity.ok(readersPage);
    }

    @GetMapping("authors")
    public ResponseEntity<Page<Author>> getAuthors(
            @RequestParam(name = "s", required = false, defaultValue = "") String search,
            @RequestParam(name = "field", required = false, defaultValue = "id") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page) {

        int size = blogConfiguration.getPageSize();
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);
        Page<Author> authorsPage = authorService.getAuthors(search, pageable);

        return ResponseEntity.ok(authorsPage);
    }

    @GetMapping("reader/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Reader> getReaderById(@PathVariable UUID id) {
        Reader reader = readerService.getReaderById(id);
        return ResponseEntity.ok(reader);
    }

    @GetMapping("author/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Author> getAuthorById(@PathVariable UUID id) {
        Author author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> createUser(@Valid @RequestBody AdminUserCreateDto adminUserCreateDto) {
        try {
            User createdUser = userService.createUser(adminUserCreateDto,
                    blogConfiguration.getVerificationApiBaseUrl());
            log.info("User: {} created successfully by admin", createdUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            log.error("Unknown error on user.create", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the user.");
        }
    }

    @PostMapping("users/{id}/permissions/update")
    public ResponseEntity<String> updatePermissions(@PathVariable UUID id,
                                                    @RequestBody PermissionSetDto permissionSetDto) {
        try {
            userService.updatePermissions(id, permissionSetDto.getPermissions());
            log.info("Permissions updated successfully!");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Permissions updated successfully!");
        } catch (Exception e) {
            log.error("Error on permission.update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unknown error during permissions updating!");
        }
    }


    @PostMapping("users/{userId}/enable")
    public ResponseEntity<String> enableUser(@PathVariable UUID userId) {
        try {
            userService.enableAccount(userId);
            log.info("Account enabled successfully for user with id: {}", userId);
            return ResponseEntity.accepted().body("Account enabled successfully!");
        } catch (Exception e) {
            log.error("Failed to enable account for user with id {}.", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to enable account.");
        }
    }

    @PostMapping("users/{userId}/disable")
    public ResponseEntity<String> disableUser(@PathVariable UUID userId) {
        try {
            userService.disableAccount(userId);
            log.info("Account disabled successfully for user with id: {}", userId);
            return ResponseEntity.accepted().body("Account disabled successfully!");
        } catch (Exception e) {
            log.error("Failed to disable account for user with id {}.", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to disable account.");
        }
    }


    @PostMapping("users/{updatedUserId}/update")
    public ResponseEntity<String> updateUser(@PathVariable UUID updatedUserId,
                                             @Valid @RequestBody AdminUserUpdateDto adminUserUpdateDto) {

        User updatedUser = userService.getUserById(updatedUserId);

        try {
            if (updatedUser instanceof Reader) {
                adminService.updateReaderByAdmin(updatedUserId, adminUserUpdateDto);
                log.info("Reader with ID {} updated successfully by admin.", updatedUserId);
            } else if (updatedUser instanceof Author) {
                adminService.updateAuthorByAdmin(updatedUserId, adminUserUpdateDto);
                log.info("Author with ID {} updated successfully by admin.", updatedUserId);
            } else {
                log.warn("User with ID {} is neither a Reader nor an Author.", updatedUserId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User update failed. User is neither a Reader nor an Author.");
            }

            log.info("User edited successfully!");
            return ResponseEntity.accepted().body("User edited successfully by admin!");
        } catch (Exception e) {
            log.error("Error on user.edit for user ID {}.", updatedUserId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during editing user by admin.");
        }

    }
}

