package com.pch777.blog.article.domain.repository;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleAuthorPanelDto;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.comment.domain.model.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Repository
public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom {

    public static final String TITLE_URL = "titleUrl";
    public static final String TITLE = "title";
    public static final String VIEWS = "views";
    public static final String LIKES = "likes";
    public static final String CREATED = "created";
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ArticleAuthorPanelDto> findSummaryByAuthorIdAndTitle(UUID authorId, String title, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ArticleAuthorPanelDto> query = cb.createQuery(ArticleAuthorPanelDto.class);

        Root<Article> articleRoot = query.from(Article.class);
        Join<Article, Category> categoryJoin = articleRoot.join("category", JoinType.LEFT);
        Join<Article, ArticleStats> statsJoin = articleRoot.join("stats", JoinType.LEFT);

        Subquery<Long> commentCountSubquery = query.subquery(Long.class);
        Root<Comment> commentSubRoot = commentCountSubquery.from(Comment.class);
        commentCountSubquery.select(cb.count(commentSubRoot))
                .where(cb.equal(commentSubRoot.get("article").get("id"), articleRoot.get("id")));

        query.select(cb.construct(ArticleAuthorPanelDto.class,
                        articleRoot.get("id"),
                        articleRoot.get(TITLE),
                        articleRoot.get(TITLE_URL),
                        categoryJoin.get("name"),
                        articleRoot.get(CREATED),
                        statsJoin.get(VIEWS),
                        statsJoin.get(LIKES),
                        commentCountSubquery.getSelection()
                ))
                .where(
                        cb.and(
                                cb.equal(articleRoot.get("author").get("id"), authorId),
                                cb.like(cb.lower(articleRoot.get(TITLE)), "%" + title.toLowerCase() + "%")
                        )
                )
                .groupBy(
                        articleRoot.get("id"),
                        articleRoot.get(TITLE),
                        articleRoot.get(TITLE_URL),
                        categoryJoin.get("name"),
                        articleRoot.get(CREATED),
                        statsJoin.get(VIEWS),
                        statsJoin.get(LIKES)
                );

        TypedQuery<ArticleAuthorPanelDto> typedQuery = entityManager.createQuery(query);
        List<ArticleAuthorPanelDto> resultList = typedQuery.getResultList();

        Comparator<ArticleAuthorPanelDto> comparator = null;
        for (Sort.Order order : pageable.getSort()) {
            Comparator<ArticleAuthorPanelDto> fieldComparator = switch (order.getProperty()) {
                case TITLE ->
                        Comparator.comparing(ArticleAuthorPanelDto::getTitle, Comparator.nullsLast(String::compareToIgnoreCase));
                case TITLE_URL ->
                        Comparator.comparing(ArticleAuthorPanelDto::getTitleUrl, Comparator.nullsLast(String::compareToIgnoreCase));
                case "category" ->
                        Comparator.comparing(ArticleAuthorPanelDto::getCategoryName, Comparator.nullsLast(String::compareToIgnoreCase));
                case CREATED ->
                        Comparator.comparing(ArticleAuthorPanelDto::getCreated, Comparator.nullsLast(LocalDateTime::compareTo));
                case VIEWS -> Comparator.comparingInt(ArticleAuthorPanelDto::getViews);
                case LIKES -> Comparator.comparingInt(ArticleAuthorPanelDto::getLikes);
                case "comments" -> Comparator.comparingLong(ArticleAuthorPanelDto::getTotalComments);
                default -> throw new IllegalArgumentException("Unknown sort property: " + order.getProperty());
            };
            if (fieldComparator != null) {
                if (order.isDescending()) {
                    fieldComparator = fieldComparator.reversed();
                }
                comparator = comparator == null ? fieldComparator : comparator.thenComparing(fieldComparator);
            }
        }
        if (comparator != null) {
            resultList.sort(comparator);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), resultList.size());
        List<ArticleAuthorPanelDto> paginatedList = resultList.subList(start, end);

        long totalCount = getTotalCount(authorId, title);

        return new PageImpl<>(paginatedList, pageable, totalCount);
    }

    private long getTotalCount(UUID authorId, String title) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Article> countRoot = countQuery.from(Article.class);

        countQuery.select(cb.count(countRoot))
                .where(
                        cb.and(
                                cb.equal(countRoot.get("author").get("id"), authorId),
                                cb.like(cb.lower(countRoot.get(TITLE)), "%" + title.toLowerCase() + "%")
                        )
                );

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}