package com.pch777.blog.article.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity(name = "user_article_likes")
public class UserArticleLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private LocalDateTime created;

    @PrePersist
    void prePersist() {
        this.created = LocalDateTime.now();
    }

}