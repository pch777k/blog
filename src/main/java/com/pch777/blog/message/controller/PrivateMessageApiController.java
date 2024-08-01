package com.pch777.blog.message.controller;


import com.pch777.blog.message.dto.ReceivePrivateMessageDto;
import com.pch777.blog.message.dto.PrivateMessageDto;
import com.pch777.blog.message.dto.SentPrivateMessageDto;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.identity.user.domain.model.User;
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
@RequestMapping("/api/v1/messages")
public class PrivateMessageApiController {

    private final PrivateMessageService privateMessageService;
    private final UserService userService;

    @PostMapping("/sent")
    public ResponseEntity<List<SentPrivateMessageDto>> getSentMessages(@AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.getUserByUsername(currentUser.getUsername());
        List<SentPrivateMessageDto> sentMessages = privateMessageService.getSentMessages(user.getId());
        return ResponseEntity.ok(sentMessages);
    }

    @PostMapping("/received")
    public ResponseEntity<List<ReceivePrivateMessageDto>> getReceivedMessages(@AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.getUserByUsername(currentUser.getUsername());
        List<ReceivePrivateMessageDto> receivedMessages = privateMessageService.getReceivedMessages(user.getId());
        return ResponseEntity.ok(receivedMessages);
    }

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<String> sendPrivateMessage(@PathVariable UUID receiverId, @AuthenticationPrincipal UserDetails currentUser,
                                                     @Valid @RequestBody PrivateMessageDto privateMessageDto) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User sender = userService.getUserByUsername(currentUser.getUsername());
        try {
            privateMessageService.sendPrivateMessage(sender.getId(), receiverId, privateMessageDto);
            return ResponseEntity.ok("Message sent successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
