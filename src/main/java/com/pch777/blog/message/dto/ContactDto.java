package com.pch777.blog.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    private UUID contactId;
    private String contactName;
    private String avatarUrl;
    private int totalUnreadMessagesFromContact;

}