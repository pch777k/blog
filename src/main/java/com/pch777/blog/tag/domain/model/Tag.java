package com.pch777.blog.tag.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pch777.blog.article.domain.model.Article;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "tags")
public class Tag {

    @Id
    private UUID id;

    @Column(unique = true)
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable
    @JsonIgnoreProperties("tags")
    private Set<Article> articles = new HashSet<>();

    public Tag() {
        this.id = UUID.randomUUID();
    }

    public void addArticle(Article article) {
        articles.add(article);
        article.getTags().add(this);
    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.getTags().remove(this);
    }

    public void removeArticles() {
        Tag self = this;
        articles.forEach(article -> article.getTags().remove(self));
        articles.clear();
    }
}
