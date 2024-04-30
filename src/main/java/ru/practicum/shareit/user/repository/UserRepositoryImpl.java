package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    private int generatedID = 0;


    @Override
    public User save(User newUser) {
        for (User existingUser : users.values()) {
            if (existingUser.getName().equals(newUser.getName())) {
                throw new EntityAlreadyExistsException(User.class, "Пользователь с именем " +
                        newUser.getName() + " уже зарегистрирован.");
            }
            if (existingUser.getEmail().equals(newUser.getEmail())) {
                throw new EntityAlreadyExistsException(User.class, "Пользователь с электронной почтой " +
                        newUser.getEmail() + " уже зарегистрирован.");
            }
        }

        newUser.setId(generateID());
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    @Override
    public User update(Integer userID, User updatedUser) {
        int idToUpdate = userID;

        if (!users.containsKey(idToUpdate)) {
            throw new EntityNotFoundException(User.class, "Пользователь с id " + idToUpdate + " не найден.");
        }

        for (User user : users.values()) {
            if (user.getId() != idToUpdate && user.getName().equals(updatedUser.getName())) {
                throw new EntityAlreadyExistsException(User.class, "Пользователь с именем " +
                        updatedUser.getName() + " уже зарегистрирован.");
            }
            if (user.getId() != idToUpdate && user.getEmail().equals(updatedUser.getEmail())) {
                throw new EntityAlreadyExistsException(User.class, "Пользователь с электронной почтой " +
                        updatedUser.getEmail() + " уже зарегистрирован.");
            }
        }

        User userToUpdate = users.get(idToUpdate);
        if (updatedUser.getName() != null) {
            userToUpdate.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            userToUpdate.setEmail(updatedUser.getEmail());
        }

        return userToUpdate;
    }

    @Override
    public User findById(Integer userID) {
        User user = users.get(userID);
        if (user == null) {
            throw new EntityNotFoundException(User.class, "Пользователь с ID " + userID + " не найден.");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public boolean deleteById(Integer userID) {
        users.remove(userID);
        return true;
    }


    private int generateID() {
        return ++generatedID;
    }

}