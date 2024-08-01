package com.pch777.blog.article.domain.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;


class ArticleTest {

    private Article article;


    @BeforeEach
    public void setUp() {
        article = new Article();
        article.setId(UUID.randomUUID());
        article.setTitle("Title");
        article.setContent("Content");
    }

    @Test
    @Disabled
    void testPrePersist() {

        //article.prePersist();
//        Assertions.assertNotNull(article.getCreated());
        Assertions.assertNotNull(article.getModified());
        Assertions.assertNotNull(article.getTitleUrl());
    }

    @Test
    void testPreUpdate() {
      //  Article article = new Article("Test Title", "Test Content");
        //article.prePersist();

        LocalDateTime originalModified = article.getModified();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        article.setTitle("Updated Title");
        article.preUpdate();

        Assertions.assertNotEquals(originalModified, article.getModified());
//        Assertions.assertNotNull(article.getTitleUrl());
    }

    @Test
    void testGenerateUrlFromTitleAndId() {
        String title = "Test Title";
        UUID id = UUID.randomUUID();
        String expectedUrl = generateUrlFromTitleAndId(title, id);
        Assertions.assertEquals(expectedUrl, "test-title-" + id.toString().substring(id.toString().length() - 6));
    }

    private int calculateExpectedTimeToRead(String title, String content) {
        int totalWords = calculateTotalWords(title + " " + content);
        return (int) Math.ceil((double) totalWords / 200);
    }

    private int calculateTotalWords(String fullContent) {
        String[] words = fullContent.split("\\s+");
        return words.length;
    }

    private String generateUrlFromTitleAndId(String title, UUID id) {
        String idSuffix = id.toString().substring(id.toString().length() - 6);
        title = title.trim() + "-" + idSuffix;
        title = replacePolishCharactersAndConvertToLowerCase(title);
        return title.replaceAll("\\s+", "-");
    }

    private String replacePolishCharactersAndConvertToLowerCase(String text) {
        return text.toLowerCase()
                .replace("ą", "a")
                .replace("ć", "c")
                .replace("ę", "e")
                .replace("ł", "l")
                .replace("ń", "n")
                .replace("ó", "o")
                .replace("ś", "s")
                .replace("ź", "z")
                .replace("ż", "z");
    }

}