package com.pch777.blog.mail;

import com.pch777.blog.common.configuration.BlogConfiguration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;
    private final BlogConfiguration blogConfiguration;

    @Override
    public void sendEmail(String toAddress, String subject, String body) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(body, true);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setFrom(new InternetAddress(blogConfiguration.getFromAddress(), blogConfiguration.getFromName()));
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error sending email: " + e.getMessage());
        }


    }
}
