package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingTimeState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.username=postgres",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceIntegrationTest {

    private final EntityManager em;

    private final ItemService service;


    @Test
    void add() {
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        em.flush();

        ItemDto itemDto = ItemDto.builder()
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        Item savedItem = service.add(owner.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item foundItem = query.setParameter("id", savedItem.getId())
                .getSingleResult();

        assertNotNull(foundItem.getId());
        assertEquals(itemDto.getName(), foundItem.getName());
        assertEquals(itemDto.getDescription(), foundItem.getDescription());
    }

    @Test
    void update() {
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        em.flush();

        Item item = new Item(null, "item name", "item description",
                true, owner, null);
        em.persist(item);
        em.flush();

        ItemDto itemDto = ItemDto.builder()
                .name("updated name")
                .description("updated description")
                .available(true)
                .build();

        Item updatedItem = service.update(owner.getId(), item.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.id = :id", Item.class);
        Item foundItem = query.setParameter("id", updatedItem.getId())
                .getSingleResult();

        assertEquals(itemDto.getName(), foundItem.getName());
        assertEquals(itemDto.getDescription(), foundItem.getDescription());
    }

    @Test
    void getItem() {
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        em.flush();

        Item item = new Item(null, "item name", "item description",
                true, owner, null);
        em.persist(item);
        em.flush();

        ItemDtoOut itemDtoOut = service.getItem(owner.getId(), item.getId());

        assertNotNull(itemDtoOut.getId());
        assertEquals(item.getName(), itemDtoOut.getName());
        assertEquals(item.getDescription(), itemDtoOut.getDescription());
    }

    @Test
    void getItems() {
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        em.flush();

        Item item1 = new Item(null, "item1 name", "item1 description",
                true, owner, null);
        Item item2 = new Item(null, "item2 name", "item2 description",
                true, owner, null);
        em.persist(item1);
        em.persist(item2);
        em.flush();

        List<ItemDtoOut> items = service.getItems(owner.getId(), 0, 10);

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getName().equals(item1.getName())));
        assertTrue(items.stream().anyMatch(item -> item.getName().equals(item2.getName())));
    }

    @Test
    void search() {
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        em.flush();

        Item item1 = new Item(null, "item1 name", "item1 description",
                true, owner, null);
        Item item2 = new Item(null, "item2 name", "another description",
                true, owner, null);
        em.persist(item1);
        em.persist(item2);
        em.flush();

        List<Item> items = service.search("item");

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getName().equals(item1.getName())));
    }

    @Test
    void addComment() {
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        em.flush();

        User booker = new User(null, "booker", "booker@email.com");
        em.persist(booker);
        em.flush();

        Item item = new Item(null, "item name", "item description",
                true, owner, null);
        em.persist(item);
        em.flush();

        Booking booking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, booker, BookingState.APPROVED, BookingTimeState.ALL);
        em.persist(booking);
        em.flush();

        ItemDto.ItemCommentDto commentDto = ItemDto.ItemCommentDto.builder()
                .text("text!")
                .build();

        ItemDto.ItemCommentDto savedComment = service.addComment(booker.getId(), item.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("SELECT c" +
                                                           " FROM ru.practicum.shareit.item.model.Comment c" +
                                                           " WHERE c.id = :id", Comment.class);
        Comment foundComment = query.setParameter("id", savedComment.getId())
                .getSingleResult();

        assertNotNull(foundComment.getId());
        assertEquals(commentDto.getText(), foundComment.getText());
    }

}