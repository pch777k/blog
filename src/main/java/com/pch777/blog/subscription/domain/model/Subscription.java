package com.pch777.blog.subscription.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.reader.domain.model.Reader;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "subscriptions")
@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Reader reader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Author author;

    private LocalDateTime created;

    public Subscription(Reader reader, Author author) {
        this.reader = reader;
        this.author = author;
    }

    @PrePersist
    void prePersist() {
        this.created = LocalDateTime.now();
    }
}
