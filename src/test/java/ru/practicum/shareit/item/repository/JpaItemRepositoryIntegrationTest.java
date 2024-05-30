package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.JpaRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class JpaItemRepositoryIntegrationTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaItemRepository itemRepository;

    @Autowired
    private JpaRequestRepository requestRepository;

    private User user1;

    private User user2;

    private Request request1;

    private Request request2;

    private Item item1;

    @BeforeEach
    public void addUserItemsAndBookings() {

        user1 = userRepository.save(User.builder()
                .name("user1")
                .email("user1@email.com")
                .build());

        user2 = userRepository.save(User.builder()
                .name("user2")
                .email("user2@email.com")
                .build());

        request1 = requestRepository.save(Request.builder()
                 .description("request1 description")
                 .requestor(user1)
                 .created(LocalDateTime.now())
                .build());

        request2 = requestRepository.save(Request.builder()
                .description("request2 description")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build());

        item1 = itemRepository.save(Item.builder()
                .name("item1 name")
                .description("item1 description")
                .available(true)
                .owner(user1)
                .requestId(request1.getId())
                .build());

        itemRepository.save(Item.builder()
                .name("item2 name")
                .description("item2 description")
                .available(true)
                .owner(user2)
                .requestId(request2.getId())
                .build());

    }

    @AfterEach
    public void removeUsersItemsAndBookings() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findByNameAndDescription() {
        Optional<Item> foundItem = itemRepository.findByNameAndDescription(item1.getName(), item1.getDescription());
        assertTrue(foundItem.isPresent());
        assertEquals(item1.getId(), foundItem.get().getId());
    }

    @Test
    void searchByNameAndDescription() {
        List<Item> items = itemRepository.searchByNameAndDescription("item");
        assertEquals(2, items.size());
    }

    @Test
    void findAllByRequestIdIn() {
        List<Item> items = itemRepository.findAllByRequestIdIn(Arrays.asList(request1.getId(), request2.getId()));
        assertEquals(2, items.size());
    }

    @Test
    void findByOwnerId() {
        List<Item> items = itemRepository.findByOwnerId(user1.getId(), PageRequest.of(0, 10));
        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
    }

}