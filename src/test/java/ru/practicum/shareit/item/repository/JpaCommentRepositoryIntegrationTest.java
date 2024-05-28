package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaCommentRepositoryIntegrationTest {

    @Autowired
    private JpaCommentRepository commentRepository;

    @Autowired
    private JpaItemRepository itemRepository;

    @Autowired
    private JpaUserRepository userRepository;

    private Item item1;

    private Item item2;

    @BeforeEach
    void addUserItemsAndComments() {

        User user1 = userRepository.save(User.builder()
                .name("user1")
                .email("user1@email.com")
                .build());

        User user2 = userRepository.save(User.builder()
                .name("user2")
                .email("user2@email.com")
                .build());

        item1 = itemRepository.save(Item.builder()
                .name("item1 name")
                .description("item1 description")
                .available(true)
                .owner(user1)
                .requestId(null)
                .build());

        item2 = itemRepository.save(Item.builder()
                .name("item2 name")
                .description("item2 description")
                .available(true)
                .owner(user2)
                .requestId(null)
                .build());

        LocalDateTime now = LocalDateTime.now();

        commentRepository.save(Comment.builder()
                .text("comment1 text")
                .item(item1)
                .author(user1)
                .created(now)
                .build());

        commentRepository.save(Comment.builder()
                .text("comment2 text")
                .item(item1)
                .author(user2)
                .created(now)
                .build());

        commentRepository.save(Comment.builder()
                .text("comment3 text")
                .item(item2)
                .author(user1)
                .created(now)
                .build());

    }

    @AfterEach
    void removeUserItemsAndComments() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item1.getId());
        assertEquals(2, comments.size());
    }

    @Test
    void findAllByItemIdIn() {
        List<Comment> comments = commentRepository.findAllByItemIdIn(Arrays.asList(item1.getId(), item2.getId()));
        assertEquals(3, comments.size());
    }

}