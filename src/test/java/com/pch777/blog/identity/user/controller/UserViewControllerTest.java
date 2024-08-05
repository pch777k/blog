package com.pch777.blog.identity.user.controller;

import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.author.service.AuthorService;
import com.pch777.blog.identity.reader.service.ReaderService;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.ProfileDto;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.domain.model.Notification;
import com.pch777.blog.notification.domain.model.TimelineDay;
import com.pch777.blog.notification.service.NotificationService;
import com.pch777.blog.notification.service.TimelineMapper;
import com.pch777.blog.security.SecurityConfiguration;
import com.pch777.blog.security.UserDetailsServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(SecurityConfiguration.class)
@WebMvcTest(UserViewController.class)
@ExtendWith(MockitoExtension.class)
class UserViewControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private UserViewController userViewController;

    @MockBean
    private PrivateMessageService privateMessageService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private ReaderService readerService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private TimelineMapper timelineMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private User user;

    @Mock
    private Author author;

    @Mock
    private Notification notification;

    @Mock
    private TimelineDay timelineDay;

    @Mock
    private Model model;

//    @BeforeEach
//    public void setUp() {
//
//    }

    @Test
    void shouldReturnProfileViewForAuthor() {
        // Given
        when(userDetails.getUsername()).thenReturn("username");
        when(userService.getUserByUsername("username")).thenReturn(author);
        when(authorService.getAuthorById(author.getId())).thenReturn(author);
        when(notificationService.getNotificationsByUserId(author.getId())).thenReturn(Collections.singletonList(notification));
        when(timelineMapper.mapNotificationListToTimeLineDayList(anyList())).thenReturn(Collections.singletonList(timelineDay));
        when(privateMessageService.countUnreadMessages(author.getId())).thenReturn(5);
        when(notificationService.countUnreadNotifications(author.getId())).thenReturn(3);

        // When
        String viewName = userViewController.singleView(model, userDetails);

        // Then
        verify(model).addAttribute("user", author);
        verify(model).addAttribute("notifications", Collections.singletonList(notification));
        verify(model).addAttribute("timelineDays", Collections.singletonList(timelineDay));
        verify(model).addAttribute("totalUnreadMessages", 5);
        verify(model).addAttribute("totalUnreadNotifications", 3);
        verify(model).addAttribute(eq("profile"), any(ProfileDto.class));
        assertEquals("user/profile/profile", viewName);
    }

    @Test
    void singleView() {
    }

    @Test
    void updateFormView() {
    }

    @Test
    void update() {
    }

    @Test
    void updatePasswordFormView() {
    }

    @Test
    void updatePassword() {
    }
}