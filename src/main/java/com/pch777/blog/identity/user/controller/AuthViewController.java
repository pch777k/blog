package com.pch777.blog.identity.user.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.authentication.TokenExpiredException;
import com.pch777.blog.exception.resource.UserNotFoundException;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.model.VerificationToken;
import com.pch777.blog.identity.user.dto.*;
import com.pch777.blog.identity.user.service.PasswordResetTokenService;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.identity.user.service.VerificationTokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

import static com.pch777.blog.identity.user.dto.PasswordRecoveryResult.*;
import static com.pch777.blog.identity.user.dto.RegistrationResult.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AuthViewController {

    // Views
    public static final String FORGOT_PASSWORD = "auth/forgot-password";
    public static final String PASSWORD_RECOVERY_FORM = "auth/password-recovery-form";
    public static final String PASSWORD_RECOVERY_VERIFICATION = "auth/password-recovery-verification";
    public static final String REGISTER = "auth/register";
    public static final String REGISTRATION_VERIFICATION = "auth/registration-verification";

    // Model Attributes
    public static final String MESSAGE = "message";
    public static final String TITLE = "title";
    public static final String TOKEN = "token";

    private static final Map<String, String> roleRedirects = Map.of(
            "ROLE_AUTHOR", "redirect:/user/profile",
            "ROLE_ADMIN", "redirect:/admin/dashboard",
            "ROLE_READER", "redirect:/"
    );

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final BlogConfiguration blogConfiguration;

    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return REGISTER;
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, REGISTRATION_ERROR_VALIDATION.getMessage());
            log.error("Error on user.signup");
            return REGISTER;
        }
        try {
            userService.registerUser(userRegisterDto, blogConfiguration.getVerificationViewBaseUrl());
            model.addAttribute(TITLE, REGISTRATION_VERIFICATION_LINK_SENT.getTitle());
            model.addAttribute(MESSAGE, REGISTRATION_VERIFICATION_LINK_SENT.getMessage());
            log.info("User registered successfully!");
        } catch (Exception e) {
            log.error("Error on user.signup", e);
            model.addAttribute(MESSAGE, REGISTRATION_UNKNOWN_ERROR.getMessage());
            return REGISTER;
        }

        return REGISTRATION_VERIFICATION;
    }

    @PostMapping("/resend-token")
    public String resendVerificationToken(EmailDto emailDto, Model model) {
        User user = userService.getUserByEmail(emailDto.getEmail());
        verificationTokenService.resendVerificationToken(user);

        model.addAttribute(TITLE, REGISTRATION_VERIFICATION_LINK_RESENT.getTitle());
        model.addAttribute(MESSAGE, REGISTRATION_VERIFICATION_LINK_RESENT.getMessage());
        return REGISTRATION_VERIFICATION;
    }

    @GetMapping("/password/forgot")
    public String forgotPassword(Model model) {
        model.addAttribute("emailDto", new EmailDto());
        return FORGOT_PASSWORD;
    }

    @PostMapping("/password/send-email")
    public String sendPasswordResetEmail(@Valid @ModelAttribute("emailDto") EmailDto emailDto,
                                         BindingResult bindingResult,
                                         Model model) {

        if (bindingResult.hasErrors()) {
            log.error("Error occurred while processing password recovery request for email: {}", emailDto.getEmail());
            model.addAttribute(MESSAGE, ERROR_EMAIL_VALIDATION.getMessage());
            return FORGOT_PASSWORD;
        }

        try {
            User user = userService.getUserByEmail(emailDto.getEmail());
            passwordResetTokenService.deleteTokenByUser(user);
            passwordResetTokenService.sendPasswordResetToken(user);
            model.addAttribute(TITLE, LINK_SENT.getTitle());
            model.addAttribute(MESSAGE, LINK_SENT.getMessage());
            log.info("An email has been sent with a link to recover your password.");
            return PASSWORD_RECOVERY_VERIFICATION;
        } catch (UserNotFoundException e) {
            log.error("User not found with email: {}", emailDto.getEmail());
            model.addAttribute(MESSAGE, ERROR_EMAIL_NOT_FOUND.getMessage() + emailDto.getEmail());
            return FORGOT_PASSWORD;
        }
    }

    @GetMapping("/password/reset")
    public String displayResetPasswordForm(@RequestParam(TOKEN) UUID token, Model model) {
        try {
            passwordResetTokenService.validateToken(token);
            model.addAttribute(TOKEN, token);
            model.addAttribute("passwordRecoveryDto", new PasswordRecoveryDto());
            return PASSWORD_RECOVERY_FORM;
        } catch (Exception e) {
            return handleTokenException(e, token, model, false);
        }
    }

    @PostMapping("/password/reset")
    public String recoverPassword(@RequestParam(TOKEN) UUID token,
                                  @Valid @ModelAttribute("passwordRecoveryDto") PasswordRecoveryDto passwordRecoveryDto,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            log.error("Error on password validation for token: {}", token);
            model.addAttribute(MESSAGE, ERROR_PASSWORD_VALIDATION.getMessage());
            model.addAttribute(TOKEN, token);
            return PASSWORD_RECOVERY_FORM;
        }

        try {
            passwordResetTokenService.resetPassword(token, passwordRecoveryDto);
            model.addAttribute(TITLE, SUCCESS.getTitle());
            model.addAttribute(MESSAGE, SUCCESS.getMessage());
            return PASSWORD_RECOVERY_VERIFICATION;
        } catch (Exception e) {
            return handleTokenException(e, token, model, true);
        }
    }

    private String handleTokenException(Exception e, UUID token, Model model, boolean isPostRequest) {
        if (e instanceof TokenExpiredException) {
            model.addAttribute(TITLE, EXPIRED_LINK.getTitle());
            model.addAttribute(MESSAGE, EXPIRED_LINK.getMessage());
            log.error("Token has expired: {}", token);
            return PASSWORD_RECOVERY_VERIFICATION;
        } else if (e instanceof EntityNotFoundException) {
            model.addAttribute(TITLE, INVALID_LINK.getTitle());
            model.addAttribute(MESSAGE, INVALID_LINK.getMessage());
            log.error("Invalid token: {}", token);
            return PASSWORD_RECOVERY_VERIFICATION;
        } else {
            model.addAttribute(TOKEN, token);
            model.addAttribute(TITLE, UNKNOWN_ERROR.getTitle());
            model.addAttribute(MESSAGE, UNKNOWN_ERROR.getMessage());
            log.error(UNKNOWN_ERROR.getMessage(), e);
            return isPostRequest ? PASSWORD_RECOVERY_FORM : PASSWORD_RECOVERY_VERIFICATION;
        }
    }

    @GetMapping("email/verify")
    public String verifyEmail(@RequestParam(TOKEN) UUID token, Model model) {
        RegistrationResult result = verificationTokenService.verifyToken(token);
        String title = result.getTitle();
        String message = result.getMessage();
        log.info("Verification result: {} - {}", title, message);

        if (result == REGISTRATION_EXPIRED_LINK) {
            VerificationToken verificationToken = verificationTokenService.getToken(token);
            EmailDto emailDto = new EmailDto(verificationToken.getUser().getEmail());
            model.addAttribute("emailDto", emailDto);
        }

        model.addAttribute(TITLE, title);
        model.addAttribute(MESSAGE, message);
        return REGISTRATION_VERIFICATION;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            return roleRedirects.getOrDefault(role, "redirect:/login");
        }
        return "redirect:/login";
    }


}
