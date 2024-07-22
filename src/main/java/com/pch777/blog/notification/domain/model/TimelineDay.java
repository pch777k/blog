package com.pch777.blog.notification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TimelineDay {

        private LocalDate localDate;
        private List<Notification> notifications;
}
