package com.pch777.blog.message.domain.repository;

import com.pch777.blog.message.domain.model.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserContactRepository extends JpaRepository<UserContact, UUID> {

    List<UserContact> findByUserId(UUID userId);
    boolean existsByUserIdAndContactId(UUID userId, UUID contactId);
}
