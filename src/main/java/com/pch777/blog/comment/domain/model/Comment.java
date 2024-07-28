package com.pch777.blog.comment.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.identity.user.domain.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    private LocalDateTime created;

    private LocalDateTime modified;

    public Comment(String content, User user) {
        this.content = content;
        this.user = user;
    }

    @PrePersist
    void prePersist() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        modified = LocalDateTime.now();
    }
}
