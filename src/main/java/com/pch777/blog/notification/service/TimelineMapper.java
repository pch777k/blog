package com.pch777.blog.notification.service;

import com.pch777.blog.notification.domain.model.Notification;
import com.pch777.blog.notification.domain.model.TimelineDay;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TimelineMapper {

    public List<TimelineDay> mapNotificationListToTimeLineDayList(List<Notification> notifications) {
        List<TimelineDay> timelineDays = new ArrayList<>();

        Map<LocalDate, List<Notification>> notificationsByDate = notifications.stream()
                .collect(Collectors.groupingBy(notification -> notification.getCreated().toLocalDate()));

        for (Map.Entry<LocalDate, List<Notification>> entry : notificationsByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Notification> notificationsForDay = entry.getValue();
            timelineDays.add(new TimelineDay(date, notificationsForDay));
        }
        return timelineDays;
    }
}
