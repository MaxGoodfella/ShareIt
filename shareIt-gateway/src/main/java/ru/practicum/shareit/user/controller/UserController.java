package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;


    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody UserDto newUser) {
        log.info("Start saving user {}", newUser);
        ResponseEntity<Object> response = userClient.save(newUser);
        log.info("Finish saving user {}", response);
        return response;
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") Long userId, @RequestBody UserDto user) {
        log.info("Start updating user {}", user);
        ResponseEntity<Object> response = userClient.update(userId, user);
        log.info("Finish updating user {}", response);
        return response;
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<Object> findById(@PathVariable("userId") Long userId) {
        log.info("Start fetching user with id = {}", userId);
        ResponseEntity<Object> response = userClient.findById(userId);
        log.info("Finish fetching user with id = {}", userId);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Start fetching all users");
        ResponseEntity<Object> response = userClient.findAll();
        log.info("Finish fetching all users");
        return response;
    }

    @DeleteMapping(value = "/{userId}")
    public void deleteById(@PathVariable("userId") Long userId) {
        log.info("Start deleting user with id = {}", userId);
        userClient.delete(userId);
        log.info("Finish deleting user with id = {}", userId);
    }

}