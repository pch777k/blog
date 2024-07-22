package com.pch777.blog.tag.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pch777.blog.article.domain.model.Article;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "tags")
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable
    @JsonIgnore
    private Set<Article> articles = new HashSet<>();

    public void removeArticles() {
        Tag self = this;
        articles.forEach(article -> article.getTags().remove(self));
        articles.clear();
    }
}
