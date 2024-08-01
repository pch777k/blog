package com.pch777.blog.identity.user.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.common.dto.Message;
import com.pch777.blog.identity.admin.service.AdminService;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.author.service.AuthorService;
import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.permission.dto.PermissionDisplayDto;
import com.pch777.blog.identity.permission.dto.PermissionSetDto;
import com.pch777.blog.identity.permission.service.DefaultPermissions;
import com.pch777.blog.identity.permission.service.PermissionService;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.reader.service.ReaderService;
import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.AdminUserCreateDto;
import com.pch777.blog.identity.user.dto.AdminUserUpdateDto;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.service.NotificationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pch777.blog.common.controller.ControllerUtils.addPaginationAttributes;
import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("admin")
@Slf4j
public class UserAdminViewController extends ProfileCommonViewController {

    public static final String MESSAGE = "message";
    public static final String ADMIN_USER_CREATE_FORM = "admin/user/create";
    public static final String ADMIN_USER_UPDATE_FORM = "admin/user/edit";
    public static final String REDIRECT_ADMIN = "redirect:/admin/";
    public static final String UPDATED_USER = "updatedUser";
    public static final String READER = "reader";
    public static final String AUTHOR = "author";

    private final ReaderService readerService;
    private final AuthorService authorService;
    private final AdminService adminService;
    private final BlogConfiguration blogConfiguration;
    private final PermissionService permissionService;
    private final DefaultPermissions defaultPermissions;

    public UserAdminViewController(PrivateMessageService privateMessageService,
                                   NotificationService notificationService,
                                   UserService userService,
                                   ReaderService readerService,
                                   AuthorService authorService,
                                   AdminService adminService,
                                   BlogConfiguration blogConfiguration,
                                   PermissionService permissionService,
                                   DefaultPermissions defaultPermissions) {
        super(privateMessageService, notificationService, userService);
        this.readerService = readerService;
        this.authorService = authorService;
        this.adminService = adminService;
        this.blogConfiguration = blogConfiguration;
        this.permissionService = permissionService;
        this.defaultPermissions = defaultPermissions;
    }

    @GetMapping("readers")
    public String indexReadersView(@RequestParam(name = "s", required = false, defaultValue = "") String search,
                                   @RequestParam(name = "field", required = false, defaultValue = "id") String field,
                                   @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                   Model model,
                                   @AuthenticationPrincipal UserDetails userDetails

    ) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        int size = blogConfiguration.getPageSize();

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);

        Page<Reader> readersPage = readerService.getReaders(search, pageable);
        model.addAttribute("readersPage", readersPage);
        addPaginationAttributes(model, page, field, direction, search);
        paging(model, readersPage, size);
        addProfileAttributes(model, user);

        return "admin/reader/index";
    }

    @GetMapping("authors")
    public String indexAuthorsView(@RequestParam(name = "s", required = false, defaultValue = "") String search,
                                   @RequestParam(name = "field", required = false, defaultValue = "id") String field,
                                   @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                   Model model,
                                   @AuthenticationPrincipal UserDetails userDetails

    ) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        int size = blogConfiguration.getPageSize();

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);

        Page<Author> authorsPage = authorService.getAuthors(search, pageable);
        model.addAttribute("authorsPage", authorsPage);
        addPaginationAttributes(model, page, field, direction, search);
        paging(model, authorsPage, size);
        addProfileAttributes(model, user);

        return "admin/author/index";
    }

    @GetMapping("reader/{id}")
    public String singleReaderView(@PathVariable UUID id,
                                   Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserByUsername(userDetails.getUsername());
        Reader reader = readerService.getReaderById(id);
        preparePermissionsModel(model, reader.getRole(), reader.getPermissions());
        model.addAttribute(READER, reader);
        addProfileAttributes(model, user);

        return "admin/reader/single";
    }

    @GetMapping("author/{id}")
    public String singleAuthorView(@PathVariable UUID id,
                                   Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserByUsername(userDetails.getUsername());
        Author author = authorService.getAuthorById(id);
        preparePermissionsModel(model, author.getRole(), author.getPermissions());
        model.addAttribute(AUTHOR, author);
        addProfileAttributes(model, user);

        return "admin/author/single";
    }

    private void preparePermissionsModel(Model model, Role role, Set<Permission> userPermissions) {
        List<PermissionDisplayDto> displayPermissions = defaultPermissions.getPermissionsForRole(role);
        Set<Permission> allPermissions = new HashSet<>(defaultPermissions.getDefaultPermissionsForRole(role));
        Set<Permission> noUserPermissions = permissionService.findAllPermissions();
        noUserPermissions.removeAll(allPermissions);

        PermissionSetDto form = new PermissionSetDto();
        form.setPermissions(userPermissions.stream().map(Permission::getId).collect(Collectors.toSet()));
        model.addAttribute("form", form);
        model.addAttribute("allPermissions", allPermissions);
        model.addAttribute("displayPermissions", displayPermissions);
        model.addAttribute("noUserPermissions", noUserPermissions);
    }

    @GetMapping("/user/create")
    public String createUserView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("adminUserCreateDto", new AdminUserCreateDto());
        addProfileAttributes(model, user);
        return ADMIN_USER_CREATE_FORM;
    }

    @PostMapping("/user/create")
    public String createUser(@Valid @ModelAttribute("adminUserCreateDto") AdminUserCreateDto adminUserCreateDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);
        User createdUser;
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during user registration!"));
            log.error("Error on user.create");
            return ADMIN_USER_CREATE_FORM;
        }
        try {
            createdUser = userService.createUser(adminUserCreateDto, blogConfiguration.getVerificationViewBaseUrl());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("User created successfully!"));
            log.info("User created successfully!");

        } catch (EntityExistsException | EntityNotFoundException e) {
            log.error("Error on user.create: " + e.getMessage());
            model.addAttribute(MESSAGE, Message.error(e.getMessage()));
            return ADMIN_USER_CREATE_FORM;
        } catch (Exception e) {
            log.error("Error on user.create", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during user creation!"));
            return ADMIN_USER_CREATE_FORM;
        }

        return redirectUrlByRole(createdUser.getRole()) + createdUser.getId();
    }

    private String redirectUrlByRole(Role role) {
        if (role == Role.READER) {
            return "redirect:/admin/reader/";
        } else {
            return "redirect:/admin/author/";
        }
    }

    @PostMapping("{userType}/{id}/permissions/update")
    public String updatePermissions(@PathVariable String userType,
                                    @PathVariable UUID id,
                                    @ModelAttribute("form") PermissionSetDto form,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        try {
            userService.updatePermissions(id, form.getPermissions());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Permissions updated successfully!"));
            log.info("Permissions updated successfully!");
        } catch (Exception e) {
            log.error("Error on permission.update", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during permissions updating!"));
            return REDIRECT_ADMIN + userType + "/" + id;
        }

        return REDIRECT_ADMIN + userType + "/" + id;
    }

    @PostMapping("{userType}/{userId}/enable")
    public String enableUser(@PathVariable String userType,
                             @PathVariable UUID userId) {
        userService.enableAccount(userId);
        return REDIRECT_ADMIN + userType + "/" + userId;
    }

    @PostMapping("{userType}/{userId}/disable")
    public String disableUser(@PathVariable String userType,
                              @PathVariable UUID userId) {
        userService.disableAccount(userId);
        return REDIRECT_ADMIN + userType + "/" + userId;
    }

    @GetMapping("{userType}/{updatedUserId}/update")
    public String updateUserView(@PathVariable String userType,
                                 @PathVariable UUID updatedUserId, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        AdminUserUpdateDto adminUserUpdateDto = new AdminUserUpdateDto();

        if (userType.equals(READER)) {
            Reader updatedUser = readerService.getReaderById(updatedUserId);
            populateAdminUserUpdateDto(adminUserUpdateDto, updatedUser);
            model.addAttribute(UPDATED_USER, updatedUser);
        } else {
            Author updatedUser = authorService.getAuthorById(updatedUserId);
            populateAdminUserUpdateDto(adminUserUpdateDto, updatedUser);
            adminUserUpdateDto.setBio(updatedUser.getBio());
            model.addAttribute(UPDATED_USER, updatedUser);
        }

        model.addAttribute("adminUserUpdateDto", adminUserUpdateDto);
        addProfileAttributes(model, user);

        return ADMIN_USER_UPDATE_FORM;
    }

    private void populateAdminUserUpdateDto(AdminUserUpdateDto adminUserUpdateDto, User user) {
        adminUserUpdateDto.setFirstName(user.getFirstName());
        adminUserUpdateDto.setLastName(user.getLastName());
        adminUserUpdateDto.setAvatarUrl(user.getAvatarUrl());
    }

    @PostMapping("{userType}/{updatedUserId}/update")
    public String updateUser(@PathVariable String userType,
                             @PathVariable UUID updatedUserId,
                             @Valid @ModelAttribute("adminUserUpdateDto") AdminUserUpdateDto adminUserUpdateDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        if (userDetails == null) {
            model.addAttribute("error", "You must be logged in to perform this action!");
            return "redirect:/login";
        }
        User user = userService.getUserByUsername(userDetails.getUsername());
        User updatedUser = userService.getUserById(updatedUserId);
        addProfileAttributes(model, user);

        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during user edition!"));
            model.addAttribute(UPDATED_USER, updatedUser);
            log.error("Error on user.edit");
            return ADMIN_USER_UPDATE_FORM;
        }

        try {
            switch (userType) {
                case READER -> adminService.updateReaderByAdmin(updatedUserId, adminUserUpdateDto);
                case AUTHOR -> adminService.updateAuthorByAdmin(updatedUserId, adminUserUpdateDto);
                default -> throw new IllegalArgumentException("Invalid user type: " + userType);
            }
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("User edited successfully!"));
            log.info("User edited successfully!");
        } catch (Exception e) {
            log.error("Error on user.edit", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during user edition!"));
            return ADMIN_USER_UPDATE_FORM;
        }

        return REDIRECT_ADMIN + userType + "/" + updatedUserId;
    }

}

