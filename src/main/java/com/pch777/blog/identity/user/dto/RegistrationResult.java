package com.pch777.blog.identity.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegistrationResult {
    REGISTRATION_SUCCESS("Registration process - Success", "Verification successful. Your account is now active."),
    REGISTRATION_INVALID_LINK("Registration process - Invalid link", "Oops! The verification link you clicked is invalid. Please check your email for the correct link."),
    REGISTRATION_EXPIRED_LINK("Registration process - Expired link", "Oops! Verification link has expired. Please click the button below to send a new verification email."),
    REGISTRATION_EMAIL_ALREADY_VERIFIED("Registration process - Email already verified", "Account is active, email already verified. You can log in."),
    REGISTRATION_VERIFICATION_LINK_SENT("Registration process - account created, link sent", "Account created, verification link sent to your email."),
    REGISTRATION_VERIFICATION_LINK_RESENT("Registration process - link resent", "The verification link has expired. A new verification link has been sent to your email."),
    REGISTRATION_ERROR_VALIDATION("Registration process - Error validation", "Error on user validation during registration."),
    REGISTRATION_UNKNOWN_ERROR("Registration process - Unknown error", "Unknown error during registration.");

    private final String title;
    private final String message;
}
