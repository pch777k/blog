package com.pch777.blog.message.domain.repository;

import com.pch777.blog.message.domain.model.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, UUID> {
    List<PrivateMessage> findByReceiverId(UUID userId);

    List<PrivateMessage> findBySenderIdAndDeletedBySenderFalse(UUID userId);

    @Query("SELECT COUNT(pm) FROM private_messages pm WHERE pm.receiver.id = :receiverId AND pm.isRead = false")
    int countByReceiverIdAndIsReadFalse(@Param("receiverId") UUID receiverId);

    @Query("SELECT COUNT(pm) FROM private_messages pm WHERE pm.receiver.id = :receiverId AND pm.sender.id = :senderId AND pm.isRead = false")
    int countByReceiverIdAndSenderIdAndIsReadFalse(@Param("receiverId") UUID receiverId, @Param("senderId") UUID senderId);

    List<PrivateMessage> findByReceiverIdAndDeletedByReceiverFalse(UUID userId);
    @Query("SELECT m FROM private_messages m WHERE (m.sender.id = :currentUserId AND m.receiver.id = :otherUserId) " +
            "OR (m.sender.id = :otherUserId AND m.receiver.id = :currentUserId) " +
            "ORDER BY m.timestamp ASC")
    List<PrivateMessage> findConversation(UUID currentUserId, UUID otherUserId);


@Query("SELECT p FROM private_messages p " +
        "WHERE p.timestamp = (SELECT MAX(pm.timestamp) " +
        "FROM private_messages pm " +
        "WHERE (pm.sender.id = :userId OR pm.receiver.id = :userId) " +
        "AND ((pm.sender.id = p.sender.id AND pm.receiver.id = p.receiver.id) " +
        "OR (pm.sender.id = p.receiver.id AND pm.receiver.id = p.sender.id))) " +
        "ORDER BY p.timestamp DESC")
    List<PrivateMessage> findLastMessagesWithContacts(@Param("userId") UUID userId);

    @Query("SELECT pm FROM private_messages pm WHERE pm.receiver.id = :currentUserId AND pm.sender.id = :contactId AND pm.isRead = false")
    List<PrivateMessage> findUnreadMessages(@Param("currentUserId") UUID currentUserId, @Param("contactId") UUID contactId);

    @Query("SELECT COUNT(pm) FROM private_messages pm WHERE pm.receiver.id = :userId AND pm.sender.id = :contactId AND pm.isRead = false")
    int countUnreadMessages(@Param("userId") UUID userId, @Param("contactId") UUID contactId);
}
