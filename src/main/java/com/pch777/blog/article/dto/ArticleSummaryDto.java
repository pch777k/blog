package com.pch777.blog.article.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleSummaryDto {

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

    private long totalComments;

}
