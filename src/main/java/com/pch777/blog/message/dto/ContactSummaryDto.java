package com.pch777.blog.message.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ContactSummaryDto {

    private UUID contactId;
    private String contactName;
    private String avatarUrl;
    private String shortMessage;
    private LocalDateTime lastMessageTime;
    private int unreadMessages;

}