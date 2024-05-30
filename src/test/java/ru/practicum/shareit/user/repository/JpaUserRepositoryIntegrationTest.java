package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@DataJpaTest
class JpaUserRepositoryIntegrationTest {

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    public void addUsers() {
        userRepository.save(User
                .builder()
                .name("user1")
                .email("user1@email.com")
                .build());

        userRepository.save(User
                .builder()
                .name("user2")
                .email("user2@email.com")
                .build());
    }

    @AfterEach
    public void removeUsers() {
        userRepository.deleteAll();
    }


    @Test
    void findByEmail_whenFound() {
        User foundUser = userRepository.findByEmail("user1@email.com");
        assertNotNull(foundUser);
    }

    @Test
    void findByEmail_whenNotFound() {
        User foundUser = userRepository.findByEmail("user1@emaissssssl.com");
        assertNull(foundUser);
    }

    @Test
    void findAll() {
        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
        assertEquals("user1@email.com", users.get(0).getEmail());
        assertEquals("user1", users.get(0).getName());
        assertEquals("user2@email.com", users.get(1).getEmail());
        assertEquals("user2", users.get(1).getName());
    }

}