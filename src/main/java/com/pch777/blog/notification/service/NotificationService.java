package com.pch777.blog.notification.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.notification.domain.model.Notification;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.subscription.domain.model.Subscription;
import com.pch777.blog.notification.domain.repository.NotificationRepository;
import com.pch777.blog.subscription.domain.repository.SubscriptionRepository;
import com.pch777.blog.identity.user.domain.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void createNotification(User user, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreated(LocalDateTime.now());
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForSubscribers(Article article) {
        List<Subscription> subscriptions = subscriptionRepository.findByAuthor(article.getAuthor());

        for (Subscription subscription : subscriptions) {
            Notification notification = new Notification();
            notification.setUser(subscription.getReader());
            notification.setMessage("New article published by " + article.getAuthor().getFullName() + ": " + article.getTitle());
            notification.setCreated(LocalDateTime.now());
            notification.setNotificationType(NotificationType.ARTICLE);
            notificationRepository.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserId(UUID id) {
        return notificationRepository.findAllNotificationsByUserIdOrderByCreatedDesc(id);

    }
    @Transactional(readOnly = true)
    public Notification getNotificationById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
    }

    @Transactional
    public void deleteById(UUID id) {
        notificationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Notification> getAllNotifications(String search, Pageable pageable) {
        return notificationRepository.findAllNotifications(search, pageable);
    }

    @Transactional
    public void markNotificationsAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    public int countUnreadNotifications(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}
