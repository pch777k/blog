package com.pch777.blog.comment.controller;

import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.service.CommentService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

import static com.pch777.blog.common.controller.ControllerUtils.addPaginationAttributes;
import static com.pch777.blog.common.controller.ControllerUtils.paging;

@RequestMapping("/user/comments")
@Controller
public class CommentUserViewController extends ProfileCommonViewController {

    private final CommentService commentService;
    private final BlogConfiguration blogConfiguration;

    public CommentUserViewController(PrivateMessageService privateMessageService,
                                     NotificationService notificationService,
                                     UserService userService,
                                     CommentService commentService,
                                     BlogConfiguration blogConfiguration) {
        super(privateMessageService, notificationService, userService);
        this.commentService = commentService;
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping
    public String indexView(
            @RequestParam(name = "s", required = false, defaultValue = "") String search,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = userService.getUserByUsername(userDetails.getUsername());
        int size = blogConfiguration.getCommentsPageSize();
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);

        Page<Comment> commentPage = commentService.getCommentsByUserId(user.getId(), search, pageable);

        addProfileAttributes(model, user);
        addPaginationAttributes(model, page, field, direction, search);
        paging(model, commentPage, size);

        model.addAttribute("commentPage", commentPage);

        return "user/comment/index";
    }
}
