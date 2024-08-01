package com.pch777.blog.identity.user.controller;

import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.common.dto.Message;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.author.service.AuthorService;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.reader.service.ReaderService;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.ChangePasswordDto;
import com.pch777.blog.identity.user.dto.UserUpdateDto;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.domain.model.Notification;
import com.pch777.blog.notification.domain.model.TimelineDay;
import com.pch777.blog.notification.service.NotificationService;
import com.pch777.blog.notification.service.TimelineMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("user")
public class UserViewController extends ProfileCommonViewController {

    public static final String MESSAGE = "message";
    public static final String EDIT_PROFILE_FORM = "user/profile/edit-profile";
    public static final String CHANGE_PASSWORD_FORM = "user/profile/change-password";

    private final AuthorService authorService;
    private final ReaderService readerService;
    private final TimelineMapper timelineMapper;

    public UserViewController(PrivateMessageService privateMessageService,
                              UserService userService,
                              AuthorService authorService,
                              ReaderService readerService,
                              NotificationService notificationService,
                              TimelineMapper timelineMapper) {
        super(privateMessageService, notificationService, userService);
        this.authorService = authorService;
        this.readerService = readerService;
        this.timelineMapper = timelineMapper;
    }


    @GetMapping("profile")
    public String singleView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        populateModelForUser(model, user);
        addProfileAttributes(model, user);
        return "user/profile/profile";
    }

    @GetMapping("profile/update")
    public String updateFormView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName(user.getFirstName());
        userUpdateDto.setLastName(user.getLastName());
        userUpdateDto.setAvatarUrl(user.getAvatarUrl());
        if(user instanceof Author) {
            Author author = authorService.getAuthorById(user.getId());
            userUpdateDto.setBio(author.getBio());
        }
        model.addAttribute("updateUserDto", userUpdateDto);
        addProfileAttributes(model, user);
        return EDIT_PROFILE_FORM;
    }

    @PostMapping("profile/update")
    public String update(@Valid @ModelAttribute("updateUserDto") UserUpdateDto userUpdateDto,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      @AuthenticationPrincipal UserDetails userDetails,
                      Model model
    ) {
        if (userDetails == null) {
            model.addAttribute("error", "You must be logged in to perform this action!");
            return "redirect:/login";
        }
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during user edition!"));

            log.error("Error on user.edit");
            return EDIT_PROFILE_FORM;
        }

        try {
            userService.updateUser(user.getId(), userUpdateDto);
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("User edited successfully!"));
            log.info("User edited successfully!");
        } catch (Exception e) {
            log.error("Error on user.edit", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during user edition!"));
            return EDIT_PROFILE_FORM;
        }

        return "redirect:/user/profile";
    }

    @GetMapping("profile/password/update")
    public String updatePasswordFormView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        model.addAttribute("changePasswordDto", changePasswordDto);
        addProfileAttributes(model, user);
        return CHANGE_PASSWORD_FORM;
    }

    @PostMapping("profile/password/update")
    public String updatePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal UserDetails userDetails,
                         Model model
    ) {
        if (userDetails == null) {
            model.addAttribute("error", "You must be logged in to perform this action!");
            return "redirect:/login";
        }
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);

        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during password edition!"));
            log.error("Error on password.edit");
            return CHANGE_PASSWORD_FORM;
        }

        try {
            userService.changePassword(user.getId(), changePasswordDto);
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Password edited successfully!"));
            log.info("Password edited successfully!");
        } catch (Exception e) {
            log.error("Error on password.edit", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during password edition!"));
            return CHANGE_PASSWORD_FORM;
        }

        return "redirect:/user/profile";
    }

    private void populateModelForUser(Model model, User user) {
        if (user instanceof Author) {
            Author author = authorService.getAuthorById(user.getId());
            model.addAttribute("user", author);
        } else if (user instanceof Reader) {
            Reader reader = readerService.getReaderById(user.getId());
            model.addAttribute("user", reader);
        }
        List<Notification> userNotifications = notificationService.getNotificationsByUserId(user.getId());
        List<TimelineDay> timelineDayList = timelineMapper.mapNotificationListToTimeLineDayList(userNotifications);
        model.addAttribute("notifications", userNotifications);
        model.addAttribute("timelineDays", timelineDayList);
    }

}
