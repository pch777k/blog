package com.pch777.blog.identity.reader.domain.repository;

import com.pch777.blog.identity.reader.domain.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, UUID> {
    Optional<Reader> findByUsername(String username);

    @Query("SELECT r FROM Reader r WHERE " +
            "LOWER(r.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Reader> findAllReaders(@Param("search") String search, Pageable pageable);

}
