package com.pch777.blog.subscription.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.service.NotificationService;
import com.pch777.blog.subscription.domain.model.Subscription;
import com.pch777.blog.subscription.service.SubscriptionService;
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
@RequestMapping("author")
public class SubscriptionAuthorViewController extends ProfileCommonViewController {

    private final BlogConfiguration blogConfiguration;
    private final SubscriptionService subscriptionService;

    public SubscriptionAuthorViewController(PrivateMessageService privateMessageService,
                                            NotificationService notificationService,
                                            UserService userService,
                                            BlogConfiguration blogConfiguration,
                                            SubscriptionService subscriptionService) {
        super(privateMessageService, notificationService, userService);
        this.blogConfiguration = blogConfiguration;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("subscriptions")
    public String subscribersView(Model model, @AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(name = "s", required = false, defaultValue = "") String search,
                                  @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                                  @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
                                  @RequestParam(name = "page", required = false, defaultValue = "0") int page
    ) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        int size = blogConfiguration.getPageSize();
        Pageable pageable =
                PageRequest.of(page, size, Sort.Direction.fromString(direction), field);
        Page<Subscription> subscribersPage = subscriptionService.getSubscribersByAuthorId(user.getId(), search, pageable);
        model.addAttribute("subscribersPage", subscribersPage);

        addProfileAttributes(model, user);
        addPaginationAttributes(model, page, field, direction, search);
        paging(model, subscribersPage, size);

        return "user/author/subscription/index";
    }


}
