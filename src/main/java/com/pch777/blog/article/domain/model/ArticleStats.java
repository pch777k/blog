package com.pch777.blog.article.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity(name = "article_stats")
public class ArticleStats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int views;
    private int likes;
    private int timeToRead;

    @OneToOne
    private Article article;

    public ArticleStats() {
        this.views = 0;
        this.likes = 0;
        this.timeToRead = 0;
    }

}
