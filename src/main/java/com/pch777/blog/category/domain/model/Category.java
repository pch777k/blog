package com.pch777.blog.category.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pch777.blog.article.domain.model.Article;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 25)
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "category")
    private List<Article> articles;

    public Category() {
        this.id = UUID.randomUUID();
    }
    public Category(String name) {
        this();
        this.name = name;
    }
}
