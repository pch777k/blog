package com.pch777.blog.identity.user.service;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.authentication.TokenExpiredException;
import com.pch777.blog.identity.user.domain.model.PasswordResetToken;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.repository.PasswordResetTokenRepository;
import com.pch777.blog.identity.user.domain.repository.UserRepository;
import com.pch777.blog.identity.user.dto.PasswordRecoveryDto;
import com.pch777.blog.mail.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BlogConfiguration blogConfiguration;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetToken createPasswordResetToken(User user) {
        long tokenVerificationTime = blogConfiguration.getTokenVerificationTime();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(UUID.randomUUID());
        passwordResetToken.setUser(user);
        passwordResetToken.setCreatedDate(LocalDateTime.now());
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(tokenVerificationTime));
        return passwordResetToken;
    }

    @Transactional
    public void sendPasswordResetToken(User user) {
        PasswordResetToken passwordResetToken = createPasswordResetToken(user);
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendResetPasswordEmail(user.getEmail(), passwordResetToken.getToken());
    }

    @Transactional
    public void resetPassword(UUID token, PasswordRecoveryDto passwordRecoveryDto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("PasswordToken not found"));
        if (isTokenExpired(resetToken.getToken())) {
            throw new TokenExpiredException("Token has expired");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(passwordRecoveryDto.getNewPassword()));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

    public void validateToken(UUID token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("PasswordToken not found"));

        if (isTokenExpired(passwordResetToken.getToken())) {
            throw new TokenExpiredException("Token has expired");
        }
    }

    public PasswordResetToken getToken(UUID token) {
        return passwordResetTokenRepository.findByToken(token).orElse(null);
    }

    public boolean isTokenExpired(UUID token) {
        PasswordResetToken passwordResetToken = getToken(token);
        return passwordResetToken != null && passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now());
    }

    @Transactional
    public void deleteTokenByUser(User user) {
        passwordResetTokenRepository.findByUser(user)
                .ifPresent(passwordResetTokenRepository::delete);
    }

}
