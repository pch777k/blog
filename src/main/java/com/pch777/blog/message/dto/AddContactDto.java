package com.pch777.blog.message.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddContactDto {

    private UUID userId;
    private UUID contactId;
}
