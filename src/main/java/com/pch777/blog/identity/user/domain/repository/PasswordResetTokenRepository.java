package com.pch777.blog.identity.user.domain.repository;

import com.pch777.blog.identity.user.domain.model.PasswordResetToken;
import com.pch777.blog.identity.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(UUID token);
    Optional<PasswordResetToken> findByUser(User user);

}
