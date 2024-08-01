package com.pch777.blog.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceivePrivateMessageDto {

    private UUID messageId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
}
