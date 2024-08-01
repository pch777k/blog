package com.pch777.blog.identity.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PasswordRecoveryResult {
    SUCCESS("Password recovery - Success",
            "Your password has been successfully reset. You can now log in with your new password."),
    LINK_SENT("Password recovery - Link sent", "An email has been sent with a link to recover your password."),
    INVALID_LINK("Password recovery - Invalid link", "The link is invalid. Please request a new password recovery link."),
    EXPIRED_LINK("Password recovery - Expired link", "Recovery password link has expired. Please click a button to send a new recovery password email."),
    ERROR_PASSWORD_VALIDATION("Password recovery - Error password validation", "Error on password validation during password recovery."),
    ERROR_EMAIL_VALIDATION("Password recovery - Error email validation", "Error on email validation during password recovery."),
    ERROR_EMAIL_NOT_FOUND("Password recovery - Error email not found", "User not found with email: "),
    UNKNOWN_ERROR("Password recovery - Unknown error", "Unknown error during recovering password.");

    private final String title;
    private final String message;
}
