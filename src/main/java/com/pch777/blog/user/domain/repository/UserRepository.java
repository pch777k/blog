package com.pch777.blog.user.domain.repository;

import com.pch777.blog.user.domain.model.User;
import com.pch777.blog.user.role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@Param("roleName") RoleType roleName);

    boolean existsByUsername(String username);

    @Query("from User where email = :u or username = :u")
    Optional<User> findByUsernameOrEmail(@Param("u") String username);

}
