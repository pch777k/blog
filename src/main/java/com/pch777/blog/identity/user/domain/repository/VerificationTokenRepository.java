package com.pch777.blog.identity.user.domain.repository;

import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(UUID token);

    Optional<VerificationToken> findByUser(User user);
}
