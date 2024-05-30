package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final JpaUserRepository userRepository;


    @Override
    @Transactional
    public User save(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Integer userID, User updatedUser) {

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userID),
                        "Пользователь с id " + userID + " не найден."));

        if (!Objects.equals(user.getEmail(), updatedUser.getEmail()) &&
                userRepository.findByEmail(updatedUser.getEmail()) != null) {
            throw new EntityAlreadyExistsException(User.class,
                        "Пользователь с email '" + updatedUser.getEmail() + "' уже существует.");
        }

        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }

        user.setId(userID);

        return userRepository.save(user);

    }

    @Override
    public List<User> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return userRepository.findAll(sort);
    }

    @Override
    public User findById(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                "Пользователь с id " + userId + " не найден."));
    }

    @Override
    @Transactional
    public void deleteById(Integer userID) {
        userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userID),
                        "Пользователь с id " + userID + " не найден."));

        userRepository.deleteById(userID);
    }

}