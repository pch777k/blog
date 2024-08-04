package com.pch777.blog.identity.user.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.authentication.TokenExpiredException;
import com.pch777.blog.exception.validation.UserAlreadyVerifiedException;
import com.pch777.blog.exception.authentication.UserInactiveException;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.*;
import com.pch777.blog.identity.user.service.PasswordResetTokenService;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.identity.user.service.VerificationTokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthApiController {

    private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserService userService;
    private final BlogConfiguration blogConfiguration;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto) throws Exception {
        User user = userService.registerUser(userRegisterDto, blogConfiguration.getVerificationApiBaseUrl());
        UserResponseDto responseDto = new UserResponseDto(user);
        log.info("User registered successfully: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/email/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") UUID token) {
        RegistrationResult result = verificationTokenService.verifyToken(token);

        String title = result.getTitle();
        String message = result.getMessage();

        log.info("Verification result: {} - {}", title, message);

        return switch (result) {
            case REGISTRATION_SUCCESS -> ResponseEntity.ok(message);
            case REGISTRATION_INVALID_LINK,
                 REGISTRATION_EXPIRED_LINK,
                 REGISTRATION_EMAIL_ALREADY_VERIFIED -> ResponseEntity.status(BAD_REQUEST).body(message);
            default ->
                 ResponseEntity.status(INTERNAL_SERVER_ERROR).body("An error occurred during email verification.");
        };

    }

    @PostMapping("/resend-token")
    public ResponseEntity<String> resendToken(@Valid @RequestBody EmailDto emailDto) {
        try {
            User user = userService.getUserByEmail(emailDto.getEmail());
            verificationTokenService.resendVerificationToken(user);
            log.info("Verification email sent to {} successfully.", emailDto.getEmail());
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Verification email sent to " + emailDto.getEmail() + " successfully.");
        } catch (UserAlreadyVerifiedException e) {
            return handleBadRequest(emailDto.getEmail(), "Email already verified. Account is active");
        } catch (UserInactiveException e) {
            return handleBadRequest(emailDto.getEmail(), "Account is inactive, please contact support");
        } catch (Exception e) {
            log.error("Failed to send verification email to {}.", emailDto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send verification email to " + emailDto.getEmail() + ".");
        }
    }

    private ResponseEntity<String> handleBadRequest(String email, String message) {
        log.error("{}: {}", message, email);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }


    @PostMapping("/password/send-email")
    public ResponseEntity<String> sendPasswordResetEmail(@Valid @RequestBody EmailDto emailDto) {
        User user = userService.getUserByEmail(emailDto.getEmail());
        passwordResetTokenService.deleteTokenByUser(user);
        try {
            passwordResetTokenService.sendPasswordResetToken(user);
            log.info("Password reset email sent to {} successfully.", emailDto.getEmail());
            return ResponseEntity.status(OK)
                    .body("Password reset email sent to " + emailDto.getEmail() + " successfully.");
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}.", emailDto.getEmail(), e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body("Failed to send password reset email to " + emailDto.getEmail() + ".");
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") UUID token,
                                                @Valid @RequestBody PasswordRecoveryDto passwordRecoveryDto) {
        try {
            passwordResetTokenService.resetPassword(token, passwordRecoveryDto);
            return ResponseEntity.ok("Your password has been successfully reset. You can now log in with your new password.");
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Oops! Password recovery link has expired. Please click the button below to send a new password recovery email.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Oops! The password recovery link you clicked is invalid. Please check your email for the correct link.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while resetting the password.");
        }
    }



}
