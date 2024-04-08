package com.pch777.blog.article.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SummaryArticleDto {

    private UUID id;

    private String title;

    private String titleUrl;

    private String shortContent;

    private String imageUrl;

    private UUID categoryId;

    private String categoryName;

    private LocalDateTime created;

    private int timeToRead;

    private int views;

    private int likes;


}
