package com.pch777.blog.article.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.tag.domain.model.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "articles")
public class Article {

    @Id
    private UUID id;

    private String title;

    @Column(name = "title_url")
    private String titleUrl;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "time_to_read")
    private int timeToRead;

    @ManyToMany(mappedBy = "articles", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnoreProperties("articles")
    private Set<Tag> tags = new HashSet<>();

    @JsonBackReference
    @ManyToOne
    private Category category;

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
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.titleUrl = generateUrlFromTitleAndId(this.title, this.id);
        calculateTimeToRead();
    }

    @PreUpdate
    void preUpdate() {
        modified = LocalDateTime.now();
        this.titleUrl = generateUrlFromTitleAndId(this.title, this.id);
        calculateTimeToRead();
    }

    private String generateUrlFromTitleAndId(String title, UUID id) {
        String idSuffix = id.toString().substring(id.toString().length() - 6);
        title = title.trim() + "-" + idSuffix;
        title = replacePolishCharactersAndConvertToLowerCase(title);
        return title.replaceAll("\\s+", "-");
    }

    private String replacePolishCharactersAndConvertToLowerCase(String text) {
        text = text.toLowerCase()
                .replace("ą", "a")
                .replace("ć", "c")
                .replace("ę", "e")
                .replace("ł", "l")
                .replace("ń", "n")
                .replace("ó", "o")
                .replace("ś", "s")
                .replace("ź", "z")
                .replace("ż", "z");
        return text;
    }

    public static final int WORDS_PER_MINUTE = 200;
    public void calculateTimeToRead() {
        int totalWords = calculateTotalWords();
        this.timeToRead = (int) Math.ceil((double) totalWords / WORDS_PER_MINUTE);
    }

    private int calculateTotalWords() {
        String fullContent = this.title + " " + this.content;
        String[] words = fullContent.split("\\s+");
        return words.length;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getArticles().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getArticles().remove(this);
    }

    public void removeTags() {
        Article self = this;
        tags.forEach(tag -> tag.getArticles().remove(self));
        tags.clear();
    }

}
