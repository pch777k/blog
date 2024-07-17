package com.pch777.blog.identity.author.domain.repository;

import com.pch777.blog.identity.author.domain.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    @Query("SELECT a FROM Author a WHERE " +
            "LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Author> findAllAuthors(String search, Pageable pageable);
}
