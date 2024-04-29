package com.pch777.blog.user.domain.model;

import com.pch777.blog.article.domain.model.Article;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserActivityArticle extends UserActivity {

    @ManyToOne
    private Article article;

    public UserActivityArticle(User user, Operation operation, Article article) {
        this.user = user;
        this.operation = operation;
        this.article = article;
    }

}


