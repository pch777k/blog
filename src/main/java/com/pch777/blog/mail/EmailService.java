package com.pch777.blog.mail;

import com.pch777.blog.common.configuration.BlogConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final BlogConfiguration blogConfiguration;
    private final EmailSender emailSender;

    public String createEmailBodyWithVerificationToken(UUID token, String baseVerificationUrl) {
        String verificationUrl = baseVerificationUrl + "?token=" + token.toString();
        return """
        <html>
            <body>
                <p>Please click on the following link to verify your account:</p>
                <p><a href="%s">%s</a></p>
                <p>Link is valid for %s minutes.</p>
                <p>Greetings,<br>%s</p>
            </body>
        </html>
        """.formatted(verificationUrl,
                verificationUrl,
                blogConfiguration.getTokenVerificationTime(),
                blogConfiguration.getFromName());
    }

    public String createEmailBodyWithPasswordResetToken(UUID token) {
        String passwordResetUrl = blogConfiguration.getPasswordBaseUrl() + "?token=" + token.toString();
        return """
        <html>
            <body>
                <p>If you forgot your password, please click on the following link to reset your password:</p>
                <p><a href="%s">%s</a></p>
                <p>Link is valid for %s minutes.</p>
                <p>Greetings,<br>%s</p>
            </body>
        </html>
        """.formatted(passwordResetUrl,
                passwordResetUrl,
                blogConfiguration.getPasswordVerificationTime(),
                blogConfiguration.getFromName());
    }

    public void sendVerificationEmail(String to, UUID token, String baseVerificationUrl) {
        String body = createEmailBodyWithVerificationToken(token, baseVerificationUrl);
        String subject = blogConfiguration.getSubjectVerificationEmail();
        emailSender.sendEmail(to, subject, body);
    }

    public void sendResetPasswordEmail(String to, UUID token) {
        String body = createEmailBodyWithPasswordResetToken(token);
        String subject = blogConfiguration.getSubjectResetPassword();
        emailSender.sendEmail(to, subject, body);
    }

}
