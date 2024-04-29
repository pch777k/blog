package com.pch777.blog.user.controller;

import com.pch777.blog.user.domain.model.User;
import com.pch777.blog.user.dto.UserRegisterDto;
import com.pch777.blog.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserApiController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("authors")
    public List<User> getAuthors() {
        return userService.getAuthors();
    }

    @GetMapping("admins")
    public List<User> getAdmins() {
        return userService.getAdmins();
    }

    @GetMapping("readers")
    public List<User> getReaders() {
        return userService.getReaders();
    }

    @PostMapping("readers")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerReader(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        return userService.registerReader(userRegisterDto);
    }

    @PostMapping("authors")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerAuthor(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        return userService.registerAuthor(userRegisterDto);
    }

    @PostMapping("admins")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerAdmin(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        return userService.registerAdmin(userRegisterDto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User updateUser(@PathVariable UUID id,
                           @Valid @RequestBody UserRegisterDto userRegisterDto) {
        return userService.updateUser(id, userRegisterDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }


}
