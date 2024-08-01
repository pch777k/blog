package com.pch777.blog.identity.admin.service;

import com.pch777.blog.exception.resource.UserNotFoundException;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.author.domain.repository.AuthorRepository;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.reader.domain.repository.ReaderRepository;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.repository.UserRepository;
import com.pch777.blog.identity.user.dto.AdminUserUpdateDto;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final ReaderRepository readerRepository;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateReaderByAdmin(UUID userId, AdminUserUpdateDto adminUserUpdateDto) {
        Reader reader = readerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Reader", userId));
        updateUser(reader, adminUserUpdateDto);
        readerRepository.save(reader);
        createNotification(reader);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateAuthorByAdmin(UUID userId, AdminUserUpdateDto adminUserUpdateDto) {
        Author author = authorRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Author", userId));
        updateUser(author, adminUserUpdateDto);
        author.setBio(adminUserUpdateDto.getBio());
        authorRepository.save(author);
        createNotification(author);
    }

    private void updateUser(User user, AdminUserUpdateDto adminUserUpdateDto) {
        user.setFirstName(adminUserUpdateDto.getFirstName());
        user.setLastName(adminUserUpdateDto.getLastName());
        user.setAvatarUrl(adminUserUpdateDto.getAvatarUrl());
    }

    private void createNotification(User user) {
        User creator = getUserCreator();
        notificationService.createNotification(creator,
                creator.getFullName() + " has updated account for user: " + user.getFullName(), NotificationType.USER);
    }

    private User getUserCreator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User", authentication.getName()));
    }
}
