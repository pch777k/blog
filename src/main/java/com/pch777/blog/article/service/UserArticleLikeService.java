package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.domain.model.UserArticleLike;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.article.domain.repository.ArticleStatsRepository;
import com.pch777.blog.article.domain.repository.UserArticleLikeRepository;
import com.pch777.blog.exception.resource.ArticleNotFoundException;
import com.pch777.blog.exception.resource.UserLikeArticleNotFoundException;
import com.pch777.blog.exception.validation.UserLikedException;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class UserArticleLikeService {

    private final UserArticleLikeRepository userArticleLikeRepository;
    private final ArticleStatsRepository articleStatsRepository;
    private final ArticleRepository articleRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    @Transactional
    public UserArticleLike likeArticle(User user, Article article) {
        if (userArticleLikeRepository.existsByUserAndArticle(user, article)) {
            throw new UserLikedException("User already liked the article");
        }
        UserArticleLike userArticleLike = new UserArticleLike();
        userArticleLike.setUser(user);
        userArticleLike.setArticle(article);
        ArticleStats articleStats = articleStatsRepository.findByArticleId(article.getId()).orElseThrow();
        articleStats.incrementLikes();
        articleStatsRepository.save(articleStats);
        notificationService.createNotification(user, "You liked article " + article.getTitle(), NotificationType.LIKE);
        notificationService.createNotification(article.getAuthor(), user.getFullName() + " liked your article " + article.getTitle(), NotificationType.LIKE);
        return userArticleLikeRepository.save(userArticleLike);
    }

    @Transactional
    public void unlikeArticle(UUID articleId, String username) {
        User user = userService.getUserByUsername(username);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        UserArticleLike userArticleLike = userArticleLikeRepository.findByUserAndArticle(user, article)
                .orElseThrow(() -> new UserLikeArticleNotFoundException(articleId, username));
        ArticleStats articleStats = articleStatsRepository
                .findByArticleId(articleId).orElseThrow();
        articleStats.decrementLikes();
        notificationService.createNotification(user, "You unliked article " + article.getTitle(), NotificationType.UNLIKE);
        notificationService.createNotification(article.getAuthor(), user.getFullName() + " unliked your article " + article.getTitle(), NotificationType.UNLIKE);
        userArticleLikeRepository.delete(userArticleLike);
    }

    public boolean canLikeArticle(UUID userId, UUID articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAuthority = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ARTICLE_LIKE"));
        return  authentication.getPrincipal() instanceof UserDetails &&
                hasAuthority &&
                !isArticleCreator(userId, articleId) &&
                !userLikedArticle(userId, articleId);
    }

    public boolean canEditOrDeleteArticle(UUID userId, UUID articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return  authentication != null &&
                authentication.getPrincipal() instanceof UserDetails &&
                isArticleCreator(userId, articleId);
    }

    public boolean isArticleCreator(UUID authorId, UUID articleId) {
        return articleRepository.existsByAuthorIdAndArticleId(authorId, articleId);
    }

    public boolean userLikedArticle(UUID userId, UUID articleId) {
        return userArticleLikeRepository.existsByUserIdAndArticleId(userId, articleId);
    }

    public Page<UserArticleLike> getUserArticleLikeByUserId(UUID id, String search, Pageable pageable) {
        return userArticleLikeRepository.findAllLikesByUserId(id, search, pageable);
    }
}
