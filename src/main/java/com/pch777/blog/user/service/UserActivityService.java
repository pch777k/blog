package com.pch777.blog.user.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.user.domain.model.*;
import com.pch777.blog.user.domain.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    public void addArticleOperation(User user, Operation operation, Article article) {
        UserActivity userActivity = new UserActivityArticle(user, operation, article);
        userActivityRepository.save(userActivity);
    }

    public void addCommentOperation(User user, Operation operation, Comment comment) {
        UserActivity userActivity = new UserActivityComment(user, operation, comment);
        userActivityRepository.save(userActivity);
    }
}
