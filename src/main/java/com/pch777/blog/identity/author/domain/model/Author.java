package com.pch777.blog.identity.author.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.subscription.domain.model.Subscription;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "authors")
@Entity
public class Author extends User {

    @Column(name = "bio")
    private String bio;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Article> articles;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions = new ArrayList<>();

    @Formula("(SELECT COUNT(*) FROM articles s WHERE s.author_id = id)")
    private int articleCount;

    @Formula("(SELECT COUNT(*) FROM subscriptions s WHERE s.author_id = id)")
    private int subscriberCount;

    public Author(String firstName, String lastName, String username, String password, String email, Role role) {
        super(firstName, lastName, username, password, email, role);
    }

}

