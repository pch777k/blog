package com.pch777.blog.article.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.identity.author.domain.model.Author;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(name = "title_url")
    private String titleUrl;

    @Column(length = 2147483647)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany(mappedBy = "articles", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnoreProperties("articles")
    private Set<Tag> tags = new HashSet<>();

    @JsonBackReference
    @ManyToOne
    private Category category;

    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private ArticleStats stats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Author author;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    private LocalDateTime created;

    private LocalDateTime modified;

    @PrePersist
    void prePersist() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.modified = LocalDateTime.now();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getArticles().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getArticles().remove(this);
    }

    public void removeTags() {
        Article self = this;
        tags.forEach(tag -> tag.getArticles().remove(self));
        tags.clear();
    }

    public void addComment(Comment comment) {
        comment.setArticle(this);
        comments.add(comment);
    }

}
