package com.pch777.blog.category.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pch777.blog.article.domain.model.Article;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 25)
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "category")
    private List<Article> articles;

    public Category(String name) {
        this.name = name;
    }
}
