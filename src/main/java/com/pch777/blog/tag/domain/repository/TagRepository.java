package com.pch777.blog.tag.domain.repository;

import com.pch777.blog.tag.domain.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByName(String name);

    boolean existsByName(String name);
}
