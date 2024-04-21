package com.pch777.blog.article.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArticleUtilsService {

    public String generateUrlFromTitleAndId(String title, UUID id) {
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
}
