package com.pch777.blog.identity.author.controller;

import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.author.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public List<Author> getAuthors() {
        return authorService.getAuthors();
    }

    @PostMapping
    public Author createAuthor(Author author) {
        return authorService.createAuthor(author);
    }
}
