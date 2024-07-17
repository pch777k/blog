package com.pch777.blog.identity.user.domain.repository;

import com.pch777.blog.common.dto.StatisticsDto;
import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query("from User where email = :u or username = :u")
    Optional<User> findByUsernameOrEmail(@Param("u") String username);

    List<User> findByRole(Role role);

    @Query("""
            SELECT new com.pch777.blog.common.dto.StatisticsDto(
            (SELECT COUNT(a) FROM Article a),
            (SELECT COUNT(c) FROM Comment c),
            (SELECT COUNT(s) FROM Subscription s),
            (SELECT COUNT(au) FROM Author au),
            (SELECT COUNT(r) FROM Reader r),
            (SELECT SUM(ast.likes) FROM ArticleStats ast),
            (SELECT COUNT(t) FROM Tag t))
            """)
    StatisticsDto getStatistics();
}
