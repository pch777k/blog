package com.pch777.blog.identity.admin.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.common.dto.StatisticsDto;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.domain.model.Notification;
import com.pch777.blog.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.pch777.blog.common.controller.ControllerUtils.addPaginationAttributes;
import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("/admin")
public class AdminViewController extends ProfileCommonViewController {

    private final BlogConfiguration blogConfiguration;

    public AdminViewController(PrivateMessageService privateMessageService,
                               UserService userService,
                               NotificationService notificationService,
                               BlogConfiguration blogConfiguration) {
        super(privateMessageService, notificationService, userService);
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping("dashboard")
    public String dashboardView(
            @RequestParam(name = "s", required = false, defaultValue = "") String search,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "desc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model, @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserByUsername(userDetails.getUsername());
        int size = blogConfiguration.getCommentsPageSize();
        StatisticsDto statisticsDto = userService.getStatistics();

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);
        Page<Notification> notificationPage = notificationService.getAllNotifications(search, pageable);

        addProfileAttributes(model, user);
        addPaginationAttributes(model, page, field, direction, search);
        paging(model, notificationPage, size);

        model.addAttribute("notificationPage", notificationPage);
        model.addAttribute("statistics", statisticsDto);

        return "admin/dashboard";
    }

}
