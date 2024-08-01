package com.pch777.blog.message.controller;

import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.domain.model.PrivateMessage;
import com.pch777.blog.message.dto.ContactDto;
import com.pch777.blog.message.dto.ContactSummaryDto;
import com.pch777.blog.message.dto.PrivateMessageDto;
import com.pch777.blog.message.service.UserContactService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("user/messages")
public class PrivateMessageViewController extends ProfileCommonViewController {

    private final UserContactService userContactService;

    public PrivateMessageViewController(PrivateMessageService privateMessageService,
                                        NotificationService notificationService,
                                        UserService userService,
                                        UserContactService userContactService) {
        super(privateMessageService, notificationService, userService);
        this.userContactService = userContactService;
    }

    @GetMapping("/{userId}")
    public String showMessagesWithUser(@PathVariable("userId") UUID contactId,
                                       Model model,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        prepareModelForChat(currentUser, contactId, model);
        return "user/message/chat";
    }

    @PostMapping("/{userId}")
    public String sendMessage(@PathVariable("userId") UUID contactId,
                              @Valid @ModelAttribute("privateMessageDto") PrivateMessageDto privateMessageDto,
                              BindingResult bindingResult,
                              Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        if (bindingResult.hasErrors()) {
            prepareModelForChat(currentUser, contactId, model);
            return "user/message/chat";
        }

        privateMessageService.sendPrivateMessage(currentUser.getId(), contactId, privateMessageDto);
        return "redirect:/user/messages/" + contactId;
    }

    private void prepareModelForChat(User currentUser, UUID contactId, Model model) {
        User contactUser = userService.getUserById(contactId);
        List<PrivateMessage> messages = privateMessageService.getMessagesWithUser(currentUser.getId(), contactId);
        privateMessageService.markMessagesAsRead(currentUser.getId(), contactId);

        List<User> contacts = userContactService.getContacts(currentUser.getId());
        List<ContactDto> contactDtos = contacts.stream().map(contact -> {
            int unreadMessages = privateMessageService.countUnreadMessagesFromContact(currentUser.getId(), contact.getId());
            return new ContactDto(contact.getId(), contact.getFullName(), contact.getAvatarUrl(), unreadMessages);
        }).toList();

        addProfileAttributes(model, currentUser);
        model.addAttribute("messages", messages);
        model.addAttribute("contacts", contactDtos);
        model.addAttribute("contactUser", contactUser);
        model.addAttribute("contactId", contactId);
        model.addAttribute("privateMessageDto", new PrivateMessageDto());
        model.addAttribute("canSendMessages", userService.canSendMessages(currentUser.getId()));
    }

    @GetMapping
    public String showContacts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsername(userDetails.getUsername());
        List<ContactSummaryDto> contacts = privateMessageService.getContactSummaries(currentUser.getId());

        addProfileAttributes(model, currentUser);
        model.addAttribute("contacts", contacts);
        model.addAttribute("user", currentUser);

        return "user/message/contacts";
    }

}
