package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService; // не могу с UserService запустить

//    public UserServiceTest() {
//        this.userService = new UserServiceImpl(userRepository);
//    }

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

//    @Test
//    void save_whenUserSameEmail_thenThrowEntityExistsException() {
//        User userToSave = new User(1, "username", "user@email.com");
//
//        when(userRepository.findByEmail(userToSave.getEmail())).thenReturn(userToSave);
//
//        assertThrows(EntityExistsException.class, () -> userService.save(userToSave));
//
//        verify(userRepository, never()).save(any(User.class)); // Ensure save is never called
//    }

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

        User actualUser = userService.update(userId, newUser);

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

        User actualUser = userService.update(userId, newUser);

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

        User actualUser = userService.update(userId, newUser);

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



//    @Test
//    void updateUserEmailDuplicate_thenNoEmailUpdate() {
//
//
//        User existingUser1 = new User(1, "User1 Name", "user1@gmail.com");
//        userRepository.save(existingUser1);
//
//        User existingUser2 = new User(2, "User2 Name", "user2@gmail.com");
//        userRepository.save(existingUser2);
//
//        when(userRepository.findById(existingUser1.getId())).thenReturn(Optional.of(existingUser1));
//        when(userRepository.findById(existingUser2.getId())).thenReturn(Optional.of(existingUser2));
//
//        User newUser = new User("User2 Name New", "user1@gmail.com");
//
//        assertThrows(EntityAlreadyExistsException.class, () -> userService.update(2, newUser));
//
////        userService.update(2, newUser);
////
////        System.out.println(existingUser2);
//
////        // Создаем двух существующих пользователей с разными email
////        User existingUser1 = new User();
////        existingUser1.setId(1);
////        existingUser1.setName("existingUser1");
////        existingUser1.setEmail("existing1@email.com");
////
////        User existingUser2 = new User();
////        existingUser2.setId(2);
////        existingUser2.setName("existingUser2");
////        existingUser2.setEmail("existing2@email.com");
////
////        // Создаем нового пользователя, чьего email мы попытаемся обновить
////        User newUser = new User();
////        newUser.setName("newUser");
////        newUser.setEmail(existingUser1.getEmail()); // Устанавливаем email такой же, как у первого существующего пользователя
////
////        // Указываем, что при поиске по ID должны возвращаться оба существующих пользователя
////        when(userRepository.findById(existingUser1.getId())).thenReturn(Optional.of(existingUser1));
////        when(userRepository.findById(existingUser2.getId())).thenReturn(Optional.of(existingUser2));
////        // Указываем, что при поиске по email должен возвращаться null, чтобы симулировать ситуацию, когда такой email уже используется другим пользователем
////        when(userRepository.findByEmail(existingUser1.getEmail())).thenReturn(null);
////
////        // Проверяем, что при обновлении на уже существующий email email пользователя не меняется
////        User updatedUser = userService.update(2, newUser);
////
////        // Проверяем, что email пользователя не изменился
////        assertEquals(existingUser2.getEmail(), updatedUser.getEmail());
//    }

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