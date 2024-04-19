package com.pch777.blog.tag.domain.repository;

import com.pch777.blog.tag.domain.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByName(String name);
    boolean existsByName(String name);

    @Query(value = """
            SELECT t.id, t.name
            FROM tags t
            JOIN tags_articles ta ON t.id = ta.tags_id
            JOIN article_stats ast ON ta.articles_id = ast.article_id
            GROUP BY t.id, t.name
            ORDER BY (COALESCE(SUM(ast.likes), 0) + COALESCE(SUM(ast.views), 0)) DESC
            LIMIT 10
            """,
            nativeQuery = true)
    List<Tag> find10TagsByPopularity();

}
