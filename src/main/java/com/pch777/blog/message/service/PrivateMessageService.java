package com.pch777.blog.message.service;

import com.pch777.blog.exception.authentication.ForbiddenException;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.domain.model.PrivateMessage;
import com.pch777.blog.message.domain.repository.PrivateMessageRepository;
import com.pch777.blog.message.dto.ContactSummaryDto;
import com.pch777.blog.message.dto.PrivateMessageDto;
import com.pch777.blog.message.dto.ReceivePrivateMessageDto;
import com.pch777.blog.message.dto.SentPrivateMessageDto;
import com.pch777.blog.notification.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.pch777.blog.notification.domain.model.NotificationType.PRIVATE_MESSAGE;

@Service
@RequiredArgsConstructor
public class PrivateMessageService {


    private final PrivateMessageRepository privateMessageRepository;
    private final UserService userService;
    private final PrivateMessageMapper privateMessageMapper;
    private final NotificationService notificationService;


    public void sendPrivateMessage(UUID senderId, UUID receiverId, PrivateMessageDto privateMessageDto) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send a message to yourself.");
        }

        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        if (!userService.canSendMessages(senderId)) {
            throw new ForbiddenException("You do not have permission to send messages.");
        }

        if (!userService.canReceiveMessages(receiverId)) {
            throw new ForbiddenException("The recipient does not have permission to receive messages.");
        }

        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setSender(sender);
        privateMessage.setReceiver(receiver);
        privateMessage.setContent(privateMessageDto.getContent());
        privateMessage.setTimestamp(LocalDateTime.now());
        notificationService.createNotification(sender, "You sent a message to " + receiver.getFullName(), PRIVATE_MESSAGE);
        notificationService.createNotification(receiver, "You received a message from " + sender.getFullName(), PRIVATE_MESSAGE);

        privateMessageRepository.save(privateMessage);
    }

    public List<ContactSummaryDto> getContactSummaries(UUID userId) {
        List<PrivateMessage> lastMessages = privateMessageRepository.findLastMessagesWithContacts(userId);

        List<ContactSummaryDto> contactSummaries = new ArrayList<>();
        for (PrivateMessage message : lastMessages) {
            ContactSummaryDto dto = new ContactSummaryDto();
            dto.setContactId(message.getSender().getId().equals(userId) ? message.getReceiver().getId() : message.getSender().getId());
            dto.setContactName(message.getSender().getId().equals(userId) ? message.getReceiver().getFullName() : message.getSender().getFullName());
            dto.setAvatarUrl(message.getSender().getId().equals(userId) ? message.getReceiver().getAvatarUrl() : message.getSender().getAvatarUrl());
            dto.setShortMessage(message.getContent().length() > 60 ? message.getContent().substring(0, 60) + "..." : message.getContent());
            dto.setLastMessageTime(message.getTimestamp());
            int unreadMessages = privateMessageRepository.countUnreadMessages(userId, message.getSender().getId());
            dto.setUnreadMessages(unreadMessages);

            contactSummaries.add(dto);
        }

        return contactSummaries;
    }

    public int countUnreadMessagesFromContact(UUID userId, UUID contactId) {
        return privateMessageRepository.countByReceiverIdAndSenderIdAndIsReadFalse(userId, contactId);
    }

    public int countUnreadMessages(UUID userId) {
        return privateMessageRepository.countByReceiverIdAndIsReadFalse(userId);
    }

    public List<SentPrivateMessageDto> getSentMessages(UUID userId) {
        List<PrivateMessage> sentMessages = privateMessageRepository.findBySenderIdAndDeletedBySenderFalse(userId);
        return sentMessages.stream()
                .map(privateMessageMapper::mapPrivateMessageToSentPrivateMessageDto)
                .toList();
    }

    public List<ReceivePrivateMessageDto> getReceivedMessages(UUID userId) {
        List<PrivateMessage> receivedMessages = privateMessageRepository.findByReceiverId(userId);

        return receivedMessages.stream()
                .map(privateMessageMapper::mapPrivateMessageToReceivePrivateMessageDto)
                .toList();
    }

    public void deleteSentMessage(UUID messageId, UUID userId) {
        PrivateMessage message = privateMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));

        if (!message.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the sender of this message");
        }

        message.setDeletedBySender(true);
        if (message.isDeletedBySender() && message.isDeletedByReceiver()) {
            privateMessageRepository.delete(message);
        } else {
            privateMessageRepository.save(message);
        }
    }

    public void deleteReceivedMessage(UUID messageId, UUID userId) {
        PrivateMessage message = privateMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));

        if (!message.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the receiver of this message");
        }

        message.setDeletedByReceiver(true);
        if (message.isDeletedBySender() && message.isDeletedByReceiver()) {
            privateMessageRepository.delete(message);
        } else {
            privateMessageRepository.save(message);
        }
    }


    public List<PrivateMessage> getMessagesWithUser(UUID currentUserId, UUID otherUserId) {
        return privateMessageRepository.findConversation(currentUserId, otherUserId);
    }

    public void markMessagesAsRead(UUID currentUserId, UUID contactId) {
        List<PrivateMessage> unreadMessages = privateMessageRepository.findUnreadMessages(currentUserId, contactId);
        for (PrivateMessage message : unreadMessages) {
            message.setRead(true);
        }
        privateMessageRepository.saveAll(unreadMessages);
    }
}

