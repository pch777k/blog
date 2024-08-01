package com.pch777.blog.notification.controller;

import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.domain.model.Notification;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.notification.domain.model.TimelineDay;
import com.pch777.blog.notification.service.NotificationService;
import com.pch777.blog.notification.service.TimelineMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user/notifications")
public class NotificationViewController extends ProfileCommonViewController {

    private final TimelineMapper timelineMapper;

    public NotificationViewController(PrivateMessageService privateMessageService,
                                      UserService userService,
                                      NotificationService notificationService,
                                      TimelineMapper timelineMapper) {
        super(privateMessageService, notificationService, userService);
        this.notificationService = notificationService;
        this.timelineMapper = timelineMapper;
    }

    @GetMapping
    public String notificationsView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        notificationService.markNotificationsAsRead(user.getId());
        List<Notification> userNotifications = notificationService.getNotificationsByUserId(user.getId());
        List<TimelineDay> timelineDayList = timelineMapper.mapNotificationListToTimeLineDayList(userNotifications);
        model.addAttribute("notifications", userNotifications);
        model.addAttribute("notificationIcons", NOTIFICATION_ICONS);
        model.addAttribute("timelineDays", timelineDayList);

        addProfileAttributes(model, user);

        return "user/notification/index";
    }

    private static final Map<NotificationType, String> NOTIFICATION_ICONS = Map.of(
            NotificationType.SUBSCRIPTION, "fa fa-bell bg-success",
            NotificationType.UNSUBSCRIPTION, "fa fa-bell-slash bg-danger",
            NotificationType.ARTICLE, "fas fa-envelope bg-indigo",
            NotificationType.PRIVATE_MESSAGE, "fas fa-envelope bg-secondary",
            NotificationType.LIKE, "fa fa-thumbs-up bg-success",
            NotificationType.UNLIKE, "fa fa-thumbs-down bg-danger",
            NotificationType.COMMENT, "fa fa-comment bg-info",
            NotificationType.USER, "fa fa-user bg-indigo"
    );


}
