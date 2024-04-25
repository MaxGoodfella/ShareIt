package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private UserService userService;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User save(@Valid @RequestBody User newUser) {
        log.info("Start saving user {}", newUser);
        User savedUser = userService.save(newUser);
        log.info("Finish saving user {}", savedUser);
        return savedUser;
    }

    @PatchMapping(value = "/{userId}")
    public User update(@PathVariable("userId") Integer userID, @RequestBody User user) {
        log.info("Start updating user {}", user);
        User updatedUser = userService.update(userID, user);
        log.info("Finish updating user {}", user);
        return updatedUser;
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User findById(@PathVariable("userId") Integer userID) {
        log.info("Start fetching user with id = {}", userID);
        User fetchedUser = userService.findById(userID);
        log.info("Finish fetching user with id = {}", fetchedUser.getId());
        return fetchedUser;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> findAll() {
        log.info("Start fetching all users");
        List<User> fetchedUsers = userService.findAll();
        log.info("Finish fetching all users");
        return fetchedUsers;
    }

    @DeleteMapping(value = "/{userId}")
    public boolean deleteById(@PathVariable("userId") Integer userID) {
        log.info("Start deleting user with id = {}", userID);
        boolean isDeleted = userService.deleteById(userID);
        log.info("Finish deleting user with id = {}", userID);
        return isDeleted;
    }

}