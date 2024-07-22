package com.pch777.blog.notification.domain.repository;

import com.pch777.blog.notification.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllNotificationsByUserIdOrderByCreatedDesc(UUID id);
    @Query("SELECT n FROM Notification n WHERE " +
            "LOWER(n.message) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.notificationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.user.username) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Notification> findAllNotifications(String search, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    int countByUserIdAndIsReadFalse(@Param("userId") UUID userId);
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    List<Notification> findByUserIdAndIsReadFalse(UUID userId);
}
