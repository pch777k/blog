package com.pch777.blog.notification.domain.model;

import com.pch777.blog.identity.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "notifications")
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;


    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private boolean isRead;

//    @Column(name = "is_displayed")
//    private boolean isDisplayed;
}
