package com.pch777.blog.identity.user.service;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.dto.StatisticsDto;
import com.pch777.blog.exception.ForbiddenException;
import com.pch777.blog.exception.UserNotFoundException;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.author.domain.repository.AuthorRepository;
import com.pch777.blog.identity.permission.domain.model.Permission;
import com.pch777.blog.identity.permission.domain.model.PermissionType;
import com.pch777.blog.identity.permission.domain.repository.PermissionRepository;
import com.pch777.blog.identity.permission.service.DefaultPermissions;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.reader.domain.repository.ReaderRepository;
import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.model.VerificationToken;
import com.pch777.blog.identity.user.domain.repository.UserRepository;
import com.pch777.blog.identity.user.dto.*;
import com.pch777.blog.mail.EmailService;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.notification.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pch777.blog.identity.user.domain.model.Role.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final ReaderRepository readerRepository;
    private final PermissionRepository permissionRepository;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    private final BlogConfiguration blogConfiguration;
    private final DefaultPermissions defaultPermissions;
    private final NotificationService notificationService;

//    @Transactional
//    public User registerUser(UserRegisterDto userRegisterDto, String verificationBaseUrl) throws Exception {
//        User user = createUserByRole(userRegisterDto, Role.READER);
//        VerificationToken token = verificationTokenService.createVerificationToken(user);
//        emailService.sendVerificationEmail(user.getEmail(), token.getToken(), verificationBaseUrl);
//        return user;
//    }

    @Transactional
    public User registerUser(UserRegisterDto userRegisterDto, String baseVerificationUrl) throws Exception {
        User user = createUserByRole(userRegisterDto, Role.READER);
        userRepository.save(user);
        VerificationToken token = verificationTokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), token.getToken(), baseVerificationUrl);
        notificationService.createNotification(user,
                user.getFullName() + " registered successfully.", NotificationType.USER);
        return user;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(UserCreateByAdminDto userCreateByAdminDto, String baseVerificationUrl) throws Exception {
        User creator = getUserCreator();
        User user = createUserByRole(userCreateByAdminDto, userCreateByAdminDto.getRole());
        VerificationToken token = verificationTokenService.createVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), token.getToken(), baseVerificationUrl);
        notificationService.createNotification(creator,
                creator.getFullName() + " has created user: " + user.getFullName(), NotificationType.USER);
        return user;

    }

    private User getUserCreator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User", authentication.getName()));
    }

    private User createUserByRole(UserRegisterDto userRegisterDto, Role role) {
        Set<Permission> defaultPermissionsForRole = defaultPermissions.getDefaultPermissionsForRole(role);

        User user;
        switch (role) {
            case READER -> user = new Reader(
                    userRegisterDto.getFirstName(),
                    userRegisterDto.getLastName(),
                    userRegisterDto.getUsername(),
                    passwordEncoder.encode(userRegisterDto.getPassword()),
                    userRegisterDto.getEmail(),
                    role);
            case AUTHOR, ADMIN -> user = new Author(
                    userRegisterDto.getFirstName(),
                    userRegisterDto.getLastName(),
                    userRegisterDto.getUsername(),
                    passwordEncoder.encode(userRegisterDto.getPassword()),
                    userRegisterDto.getEmail(),
                    role);
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        }

        user.setRole(role);
        user.setPermissions(defaultPermissionsForRole);
        user.setAvatarUrl(blogConfiguration.getDefaultAvatarUrl());
        user.setEnabled(false);

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

//    @Transactional
//    public User updateUser(UUID id, UserRegisterDto userRegisterDto) {
//        User updatedUser = userRepository.findById(id)
//                .orElseThrow(() -> new UserNotFoundException("User", id));
//        updatedUser.setFirstName(userRegisterDto.getFirstName());
//        updatedUser.setLastName(userRegisterDto.getLastName());
//        updatedUser.setUsername(userRegisterDto.getUsername());
//        updatedUser.setPassword(userRegisterDto.getPassword());
//        updatedUser.setEmail(userRegisterDto.getEmail());
//
//        return userRepository.save(updatedUser);
//    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_AUTHOR','ROLE_ADMIN') and hasAuthority('USER_UPDATE')")
    public User updateUser(UUID id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User", id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!user.getUsername().equals(authentication.getName())) {
            throw new ForbiddenException("Access denied");
        }

        if (user instanceof Reader reader) {
            updateCommonFields(reader, userUpdateDto);
            return readerRepository.save(reader);
        } else if (user instanceof Author author) {
            updateCommonFields(author, userUpdateDto);
            author.setBio(userUpdateDto.getBio());
            return authorRepository.save(author);
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }
    }

    private void updateCommonFields(User user, UserUpdateDto userUpdateDto) {
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());
        user.setAvatarUrl(userUpdateDto.getAvatarUrl());
        notificationService.createNotification(user, user.getFullName() + " has updated their account.", NotificationType.USER);
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public void changePassword(UUID userId, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!user.getUsername().equals(authentication.getName())) {
            throw new ForbiddenException("Access denied");
        }

        String newPasswordEncoded = passwordEncoder.encode(changePasswordDto.getNewPassword());
        user.setPassword(newPasswordEncoded);
        userRepository.save(user);
    }

    @Transactional
    public User recoverPassword(UUID userId, RecoverPasswordDto recoverPasswordDto) {

//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new UnauthorizedException("You must be logged in to change your password");
//        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
//        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
//        if (!userDetails.getUsername().equals(user.getUsername())) {
//            throw new ForbiddenException("You are not authorized to change this user's password");
//        }

//        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
//            throw new IncorrectOldPasswordException("Incorrect old password");
//        }

        String recoverPasswordEncoded = passwordEncoder.encode(recoverPasswordDto.getNewPassword());
        user.setPassword(recoverPasswordEncoded);
        return userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updatePermissions(UUID userId, Set<UUID> permissions) {
        User creator = getUserCreator();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        Set<Permission> updatedPermissions = permissions.stream()
                .map(permissionRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        user.setPermissions(updatedPermissions);
        notificationService.createNotification(creator,
                creator.getFullName() + " has updated permissions for user: " + user.getFullName(), NotificationType.USER);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void enableAccount(UUID userId) {
        User creator = getUserCreator();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        user.setEnabled(true);
        notificationService.createNotification(creator,
                creator.getFullName() + " has activated the account for user: " + user.getFullName(), NotificationType.USER);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void disableAccount(UUID userId) {
        User creator = getUserCreator();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User", userId));
        user.setEnabled(false);
        notificationService.createNotification(creator,
                creator.getFullName() + " deactivated account for user: " + user.getFullName(), NotificationType.USER);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User", email));
    }

    public StatisticsDto getStatistics() {
        return userRepository.getStatistics();
    }
}
