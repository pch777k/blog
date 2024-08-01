package com.pch777.blog.common.controller;

import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.ProfileDto;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;

@AllArgsConstructor
public abstract class ProfileCommonViewController {

    protected PrivateMessageService privateMessageService;
    protected NotificationService notificationService;
    protected UserService userService;

    protected void addProfileAttributes(Model model, User user) {
        int totalUnreadMessages = privateMessageService.countUnreadMessages(user.getId());
        int totalUnreadNotifications = notificationService.countUnreadNotifications(user.getId());
        ProfileDto profileDto = new ProfileDto();
        profileDto.setUserId(user.getId());
        profileDto.setFullName(user.getFullName());
        profileDto.setAvatarUrl(user.getAvatarUrl());
        profileDto.setRole(user.getRole());
        profileDto.setTotalUnreadMessages(totalUnreadMessages);
        profileDto.setTotalUnreadNotifications(totalUnreadNotifications);
        model.addAttribute("totalUnreadMessages", totalUnreadMessages);
        model.addAttribute("totalUnreadNotifications", totalUnreadNotifications);
        model.addAttribute("profile", profileDto);

    }
}