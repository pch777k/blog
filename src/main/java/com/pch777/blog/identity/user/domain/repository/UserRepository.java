package com.pch777.blog.identity.user.domain.repository;

import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query("from User where email = :u or username = :u")
    Optional<User> findByUsernameOrEmail(@Param("u") String username);

    List<User> findByRole(Role role);
}
