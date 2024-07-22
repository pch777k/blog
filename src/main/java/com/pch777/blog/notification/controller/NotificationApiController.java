package com.pch777.blog.notification.controller;

import com.pch777.blog.notification.domain.model.Notification;
import com.pch777.blog.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping("users/{id}")
    public List<Notification> getNotificationsByUserId(@PathVariable UUID id) {
        return notificationService.getNotificationsByUserId(id);
    }

//    @PutMapping("/{id}/display")
//    public ResponseEntity<Void> markNotificationAsRead(@PathVariable UUID id) {
//        notificationService.markNotificationAsRead(id);
//        return ResponseEntity.ok().build();
//    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteNotificationById(@PathVariable UUID id) {
        notificationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
