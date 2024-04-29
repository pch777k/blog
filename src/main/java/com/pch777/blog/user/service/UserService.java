package com.pch777.blog.user.service;

import com.pch777.blog.user.domain.model.Admin;
import com.pch777.blog.user.domain.model.Author;
import com.pch777.blog.user.domain.model.Reader;
import com.pch777.blog.user.domain.model.User;
import com.pch777.blog.user.domain.repository.UserRepository;
import com.pch777.blog.user.dto.UserRegisterDto;
import com.pch777.blog.user.role.Role;
import com.pch777.blog.user.role.RoleType;
import com.pch777.blog.user.role.RoleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.pch777.blog.user.role.RoleType.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private User registerUser(UserRegisterDto userRegisterDto, RoleType roleType) {
        if (userRepository.existsByUsername(userRegisterDto.getUsername())) {
            throw new EntityExistsException("User already exists with username " + userRegisterDto.getUsername());
        }

        Optional<Role> optionalRole = roleRepository.findByName(roleType);

        if (optionalRole.isEmpty()) {
            throw new EntityNotFoundException("Role not found with name : " + roleType.name());
        }

        User user;
        switch (roleType) {
            case READER -> user = new Reader();
            case AUTHOR -> user = new Author();
            case ADMIN -> user = new Admin();
            default -> throw new IllegalArgumentException("Invalid role: " + roleType);
        }

        user.setUsername(userRegisterDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setEmail(userRegisterDto.getEmail());
        user.setRole(optionalRole.get());

        return userRepository.save(user);
    }

    @Transactional
    public User registerReader(@Valid UserRegisterDto userRegisterDto) {
        return registerUser(userRegisterDto, READER);
    }

    @Transactional
    public User registerAuthor(@Valid UserRegisterDto userRegisterDto) {
        return registerUser(userRegisterDto, AUTHOR);
    }

    @Transactional
    public User registerAdmin(UserRegisterDto userRegisterDto) {
        return registerUser(userRegisterDto, ADMIN);
    }

    @Transactional(readOnly = true)
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getAdmins() {
        return userRepository.findByRoleName(ADMIN);
    }

    @Transactional(readOnly = true)
    public List<User> getAuthors() {
        return userRepository.findByRoleName(AUTHOR);
    }
    @Transactional(readOnly = true)
    public List<User> getReaders() {
        return userRepository.findByRoleName(READER);
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
    @Transactional
    public User updateUser(UUID id, UserRegisterDto userRegisterDto) {
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        updatedUser.setUsername(userRegisterDto.getUsername());
        updatedUser.setPassword(userRegisterDto.getPassword());
        updatedUser.setEmail(userRegisterDto.getEmail());
        return userRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }
}
