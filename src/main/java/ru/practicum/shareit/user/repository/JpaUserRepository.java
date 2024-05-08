package ru.practicum.shareit.user.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface JpaUserRepository extends JpaRepository<User, Integer> {

    User findByName(String name);

    User findByEmail(String email);

    List<User> findAll(Sort sort);

}