package com.pch777.blog.subscription.domain.repository;

import com.pch777.blog.subscription.domain.model.Subscription;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.reader.domain.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByAuthor(Author author);

    Optional<Subscription> findByAuthorIdAndReaderId(UUID authorId, UUID readerId);

    boolean existsSubscriptionByAuthorAndReader(Author author, Reader reader);

    List<Subscription> findByAuthorId(UUID id);
    Page<Subscription> findByAuthorId(UUID id, Pageable pageable);
    Page<Subscription> findByReaderId(UUID id, Pageable pageable);

    List<Subscription> findByReaderId(UUID id);

    @Query("SELECT s FROM Subscription s WHERE s.author.id = :id AND (" +
            "LOWER(s.reader.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.reader.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.reader.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Subscription> findSubscriptionsByAuthorId(@Param("id") UUID id, @Param("search") String search, Pageable pageable);

    @Query("SELECT s FROM Subscription s WHERE s.reader.id = :id AND (" +
            "LOWER(s.author.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.author.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.author.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Subscription> findSubscriptionsByReaderId(@Param("id") UUID id, @Param("search") String search, Pageable pageable);
}