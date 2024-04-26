package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryImplTest {

    private static UserRepositoryImpl userRepository;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        userRepository = new UserRepositoryImpl();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testSave() {
        User newUser = new User("user", "user@gmail.com");

        assertDoesNotThrow(() -> userRepository.save(newUser));
    }

    @Test
    public void testSaveWithNoEmail() {
        User newUser = new User("user");

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        assertFalse(violations.isEmpty(), "Отсутствует электронная почта");
    }

    @Test
    public void testSaveWithInvalidEmail() {
        User newUser = new User("user", "это-неправильный?эмейл@");

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        assertFalse(violations.isEmpty(), "Формат не соответствует формату электронной почты");
    }

    @Test
    public void testSaveWithSameEmail_ShouldThrowEntityAlreadyExistsException() {
        User newUser1 = new User("user1", "user@gmail.com");
        User newUser2 = new User("user2", "user@gmail.com");

        userRepository.save(newUser1);

        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.save(newUser2));
    }

    @Test
    public void testSaveWithSameName_ShouldThrowEntityAlreadyExistsException() {
        User newUser1 = new User("user", "user1@gmail.com");
        User newUser2 = new User("user", "user1@gmail.com");

        userRepository.save(newUser1);

        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.save(newUser2));
    }

    @Test
    public void testUpdateExistingUser() {
        User existingUser = new User("user", "user1@gmail.com");
        userRepository.save(existingUser);

        User updatedUser = new User("userupdated", "user1updated@gmail.com");
        updatedUser.setId(existingUser.getId());
        User resultUser = userRepository.update(updatedUser.getId(), updatedUser);


        assertEquals(updatedUser, resultUser, "Информация о пользователе должна быть обновлена");
        assertEquals("userupdated", resultUser.getName(), "Имя не совпадает");
        assertEquals("user1updated@gmail.com", resultUser.getEmail(), "Почта не совпадает");
    }

    @Test
    public void testUpdateUnknownUser_shouldThrowEntityNotFoundException() {
        User existingUser = new User("user1", "user1@gmail.com");
        userRepository.save(existingUser);

        User updatedUser = new User("user1updated", "user1updated@gmail.com");
        updatedUser.setId(2);

        assertThrows(EntityNotFoundException.class, () -> userRepository.update(updatedUser.getId(), updatedUser));
    }

    @Test
    public void testFindAll() {
        User user1 = new User("user1", "user1@gmail.com");
        User user2 = new User("user2", "user2@gmail.com");
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size(), "Количество элементов не совпадает");
        assertEquals(user1, users.get(0), "Первый элемент не совпадает");
        assertEquals(user2, users.get(1), "Второй элемент не совпадает");


        User updatedUser1 = new User("user1updated", "user1updated@gmail.com");
        User updatedUser2 = new User("user2updated", "user2updated@gmail.com");
        updatedUser1.setId(user1.getId());
        updatedUser2.setId(user2.getId());
        userRepository.update(updatedUser1.getId(), updatedUser1);
        userRepository.update(updatedUser2.getId(), updatedUser2);

        List<User> updatedUsers = userRepository.findAll();

        assertEquals(2, updatedUsers.size(), "Количество элементов не совпадает после обновления");
        assertEquals(updatedUser1, updatedUsers.get(0), "Первый элемент не совпадает после обновления");
        assertEquals(updatedUser2, updatedUsers.get(1), "Второй элемент не совпадает после обновления");
    }

    @Test
    public void testFindById() {
        User user1 = new User("user1", "user1@gmail.com");
        User user2 = new User("user2", "user2@gmail.com");

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        assertEquals(savedUser1, userRepository.findById(savedUser1.getId()),
                "Пользователь 1 найден");
        assertEquals(savedUser2, userRepository.findById(savedUser2.getId()),
                "Пользователь 2 найден");
    }

    @Test
    public void testFindById_shouldThrowEntityNotFoundException() {
        User user1 = new User("user1", "user1@gmail.com");
        User user2 = new User("user2", "user2@gmail.com");

        assertThrows(EntityNotFoundException.class, () -> userRepository.findById(user1.getId()),
                "Пользователь 1 не найден");
        assertThrows(EntityNotFoundException.class, () -> userRepository.findById(user2.getId()),
                "Пользователь 2 не найден");
    }

    @Test
    public void testDeleteById() {
        User user1 = new User("user1", "user1@gmail.com");
        User user2 = new User("user2", "user2@gmail.com");

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        userRepository.deleteById(savedUser2.getId());

        assertEquals(savedUser1, userRepository.findById(savedUser1.getId()),
                "Пользователь 1 найден");
        assertThrows(EntityNotFoundException.class, () -> userRepository.findById(user2.getId()),
                "Пользователь 2 не найден");
    }

}