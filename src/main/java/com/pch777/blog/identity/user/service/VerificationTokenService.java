package com.pch777.blog.identity.user.service;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.validation.UserAlreadyVerifiedException;
import com.pch777.blog.exception.authentication.UserInactiveException;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.model.VerificationToken;
import com.pch777.blog.identity.user.domain.repository.UserRepository;
import com.pch777.blog.identity.user.domain.repository.VerificationTokenRepository;
import com.pch777.blog.identity.user.dto.RegistrationResult;
import com.pch777.blog.mail.EmailService;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.notification.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.pch777.blog.identity.user.dto.RegistrationResult.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BlogConfiguration blogConfiguration;
    private final NotificationService notificationService;

    public VerificationToken save(VerificationToken verificationToken) {
        return  verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken getToken(UUID token) {

        return  verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token not found."));
    }

    @Transactional
    public void resendVerificationToken(User user) {
        VerificationToken tokenToDelete = verificationTokenRepository.findByUser(user)
                .orElse(null);

        if (tokenToDelete != null) {
            if (tokenToDelete.getConfirmationDate() != null) {
                throw new UserInactiveException("Account is inactive, please contact support");
            }
            verificationTokenRepository.delete(tokenToDelete);
            verificationTokenRepository.flush();
        }

        if (user.isEnabled()) {
            throw new UserAlreadyVerifiedException("User's email already verified. Account is active.");
        }

        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken(), blogConfiguration.getVerificationViewBaseUrl());
    }

        public VerificationToken createVerificationToken(User user) {
        long tokenVerificationTime = blogConfiguration.getTokenVerificationTime();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID());
        verificationToken.setUser(user);
        verificationToken.setCreatedDate(LocalDateTime.now());
        verificationToken.setExpirationDate(LocalDateTime.now().plusMinutes(tokenVerificationTime));
        return verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public RegistrationResult verifyToken(UUID token) {
        log.info("Attempting to verify token: {}", token);

        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);

        if (verificationTokenOpt.isEmpty()) {
            log.warn("Token not found: {}", token);
            return REGISTRATION_INVALID_LINK;
        }

        VerificationToken verificationToken = verificationTokenOpt.get();
        User user = verificationToken.getUser();
        if (verificationToken.getConfirmationDate() != null || user.isEnabled()) {
            log.info("Email already verified: {}", user.getEmail());
            return REGISTRATION_EMAIL_ALREADY_VERIFIED;
        }

        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            log.warn("Token expired: {}", token);
            return REGISTRATION_EXPIRED_LINK;
        }

        user.setEnabled(true);
        userRepository.save(user);
        verificationToken.setConfirmationDate(LocalDateTime.now());
        verificationTokenRepository.save(verificationToken);
        log.info("Token verification successful for user: {}", user.getEmail());
        notificationService.createNotification(user,
                user.getFullName() + " verified email successfully.", NotificationType.USER);

        return REGISTRATION_SUCCESS;

    }

}
