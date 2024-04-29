package com.pch777.blog.user.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.tag.domain.model.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Reader reader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Tag tag;

}
