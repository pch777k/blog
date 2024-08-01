package com.pch777.blog.message.service;

import com.pch777.blog.message.domain.model.PrivateMessage;
import com.pch777.blog.message.dto.ReceivePrivateMessageDto;
import com.pch777.blog.message.dto.SentPrivateMessageDto;
import org.springframework.stereotype.Component;

@Component
public class PrivateMessageMapper {


    public ReceivePrivateMessageDto mapPrivateMessageToReceivePrivateMessageDto(PrivateMessage privateMessage) {
        ReceivePrivateMessageDto receivePrivateMessageDto = new ReceivePrivateMessageDto();
        receivePrivateMessageDto.setMessageId(privateMessage.getId());
        receivePrivateMessageDto.setSenderName(privateMessage.getSender().getUsername());
        receivePrivateMessageDto.setContent(privateMessage.getContent());
        receivePrivateMessageDto.setTimestamp(privateMessage.getTimestamp());
        return receivePrivateMessageDto;
    }

    public SentPrivateMessageDto mapPrivateMessageToSentPrivateMessageDto(PrivateMessage privateMessage) {
        SentPrivateMessageDto sentPrivateMessageDto = new SentPrivateMessageDto();
        sentPrivateMessageDto.setMessageId(privateMessage.getId());
        sentPrivateMessageDto.setReceiverName(privateMessage.getReceiver().getUsername());
        sentPrivateMessageDto.setContent(privateMessage.getContent());
        sentPrivateMessageDto.setTimestamp(privateMessage.getTimestamp());
        return sentPrivateMessageDto;
    }
}
