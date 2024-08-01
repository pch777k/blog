package com.pch777.blog.identity.author.service;

import com.pch777.blog.exception.resource.UserNotFoundException;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.author.domain.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Author getAuthorById(UUID id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Author", id));
    }

    public Page<Author> getAuthors(String search, Pageable pageable) {
        return authorRepository.findAllAuthors(search, pageable);
    }
}
