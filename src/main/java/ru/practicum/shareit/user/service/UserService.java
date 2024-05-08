package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User save(User newUser);

    User update(Integer userID, User user);

    User findById(Integer userID);

    List<User> findAll();

    void deleteById(Integer userID);

}