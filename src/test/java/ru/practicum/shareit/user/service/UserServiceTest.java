package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;


    @Test
    void findById_whenUserFound_thenReturnUser() {
        Integer userId = 1;
        User expectedUser = new User();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));

        User actualUser = userService.findById(userId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findById_whenUserNotFound_thenThrowEntityNotFoundException() {
        Integer userId = 1;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void save_whenUserValid_thenSaveUser() {
        User userToSave = new User(1,"username", "user@email.com");

        when(userRepository.save(userToSave)).thenReturn(userToSave);

        User actualUser = userService.save(userToSave);

        assertEquals(userToSave, actualUser);

        verify(userRepository).save(userToSave);
    }

    @Test
    void save_whenUserNoEmail_thenThrowIllegalArgumentException() {
        User userToSave = new User(1,"username", null);

        assertThrows(IllegalArgumentException.class, () -> userService.save(userToSave));

        verify(userRepository, never()).save(any(User.class)); // Ensure save is never called
    }

    @Test
    void save_whenUserEmailDuplicate_thenThrowDataIntegrityViolationException() {
        User userToSave = new User(1, "username", "user@email.com");

        doThrow(DataIntegrityViolationException.class).when(userRepository).save(any(User.class));

        assertThrows(DataIntegrityViolationException.class, () -> userService.save(userToSave));
    }

    @Test
    void updateUser_whenUserFound_thenUpdateUser() {
        Integer userId = 1;
        User oldUser = new User();
        oldUser.setName("name");
        oldUser.setEmail("e@mail.com");

        User newUser = new User();
        newUser.setName("newname");
        newUser.setEmail("newe@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.update(userId, newUser);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(newUser.getName(), savedUser.getName());
        assertEquals(newUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void updateUserName_whenUserFound_thenUpdateUser() {
        Integer userId = 1;
        User oldUser = new User();
        oldUser.setName("name");
        oldUser.setEmail("e@mail.com");

        User newUser = new User();
        newUser.setName("newname");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.update(userId, newUser);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(newUser.getName(), savedUser.getName());
        assertEquals(oldUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void updateUserEmail_whenUserFound_thenUpdateUser() {
        Integer userId = 1;
        User oldUser = new User();
        oldUser.setName("name");
        oldUser.setEmail("e@mail.com");

        User newUser = new User();
        newUser.setEmail("newe@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.update(userId, newUser);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(oldUser.getName(), savedUser.getName());
        assertEquals(newUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowEntityNotFoundException() {
        Integer userId = 1;

        User newUser = new User();
        newUser.setName("newname");
        newUser.setEmail("newe@mail.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(userId, newUser));
    }

    @Test
    void findAll_thenReturnUsers() {

        User existingUser1 = new User(1, "User1 Name", "user1@gmail.com");
        User existingUser2 = new User(2, "User2 Name", "user2@gmail.com");

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        when(userRepository.findAll(sort)).thenReturn(Arrays.asList(existingUser1, existingUser2));

        List<User> users = userService.findAll();

        assertEquals(2, users.size());
        assertEquals(existingUser1, users.get(0));
        assertEquals(existingUser2, users.get(1));

    }

    @Test
    void deleteUser_whenUserFound_thenDeleteUserFromDb() {
        User user = new User();
        Integer userId = 1;
        user.setId(userId);

        doNothing().when(userRepository).deleteById(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);
    }

    @Test
    void deleteUser_whenUserNotFound_thenThrowEntityNotFoundException() {
        User user = new User();
        Integer userId = 1;
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.deleteById(userId));
    }

}