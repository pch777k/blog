package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.UserArticleLike;
import com.pch777.blog.article.service.UserArticleLikeService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
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
@RequestMapping("/user")
public class UserArticleLikeViewController extends ProfileCommonViewController {

        private final UserArticleLikeService userArticleLikeService;
        private final BlogConfiguration blogConfiguration;

    public UserArticleLikeViewController(PrivateMessageService privateMessageService,
                                         NotificationService notificationService,
                                         UserService userService,
                                         UserArticleLikeService userArticleLikeService,
                                         BlogConfiguration blogConfiguration) {
        super(privateMessageService, notificationService, userService);
        this.userArticleLikeService = userArticleLikeService;
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping("/liked-articles")
        public String getLikedArticles(Model model, @AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam(name = "s", required = false, defaultValue = "") String search,
                                       @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                                       @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
                                       @RequestParam(name = "page", required = false, defaultValue = "0") int page
        ) {
            User user = userService.getUserByUsername(userDetails.getUsername());
            int size = blogConfiguration.getCommentsPageSize();
            Pageable pageable =
                    PageRequest.of(page, blogConfiguration.getArticlesPageSize(), Sort.Direction.fromString(direction), field);
            Page<UserArticleLike> likedArticlesPage = userArticleLikeService.getUserArticleLikeByUserId(user.getId(), search, pageable);

            addProfileAttributes(model, user);
            addPaginationAttributes(model, page, field, direction, search);
            paging(model, likedArticlesPage, size);

            model.addAttribute("likedArticlesPage", likedArticlesPage);
            return "user/liked-articles/index";
        }

}
