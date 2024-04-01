package com.pch777.blog.article.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "articles")
public class Article {

    @Id
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 60)
    private String title;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @NotBlank
    private String content;

    private LocalDateTime created;

    private LocalDateTime modified;

    public Article() {
        this.id = UUID.randomUUID();
    }
    public Article(String title, String content) {
        this();
        this.title = title;
        this.content = content;
    }

    @PrePersist
    void prePersist() {
        created = LocalDateTime.now();
        modified = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        modified = LocalDateTime.now();
    }

}
