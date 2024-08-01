package com.pch777.blog.mail;

public interface EmailSender {
    void sendEmail(String toAddress, String subject, String body);
}
