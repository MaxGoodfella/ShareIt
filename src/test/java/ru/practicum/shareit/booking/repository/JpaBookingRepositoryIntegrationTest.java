package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingTimeState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class JpaBookingRepositoryIntegrationTest {

    @Autowired
    private JpaBookingRepository bookingRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaItemRepository itemRepository;

    private User user1;

    private User user2;

    private Item item1;

    private Item item2;

    private Booking booking1;

    private Booking booking2;

    @BeforeEach
    public void addUserItemsAndBookings() {
        LocalDateTime createdDateTime = LocalDateTime.now();

        user1 = userRepository.save(User.builder()
                .name("user1")
                .email("user1@email.com")
                .build());

        user2 = userRepository.save(User.builder()
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

        booking1 = bookingRepository.save(Booking.builder()
                .start(createdDateTime.plusDays(1))
                .end(createdDateTime.plusDays(2))
                .item(item1)
                .booker(user1)
                .status(BookingState.WAITING)
                .bookingTimeState(BookingTimeState.FUTURE)
                .build());

        booking2 = bookingRepository.save(Booking.builder()
                .start(createdDateTime.plusDays(3))
                .end(createdDateTime.plusDays(4))
                .item(item2)
                .booker(user2)
                .status(BookingState.APPROVED)
                .bookingTimeState(BookingTimeState.FUTURE)
                .build());
    }

    @AfterEach
    public void removeUsersItemsAndBookings() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findByItemIdAndBookerId() {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(item1.getId(), user1.getId());
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByItemAndStatusOrderByStartAsc() {
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(item2, BookingState.APPROVED);
        assertEquals(1, bookings.size());
        assertEquals(booking2.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByItemInAndStatusOrderByStartAsc() {
        List<Booking> bookings = bookingRepository
                .findAllByItemInAndStatusOrderByStartAsc(Arrays.asList(item1, item2), BookingState.WAITING);
        assertEquals(1, bookings.size());
        assertEquals(booking1.getId(), bookings.get(0).getId());
    }

    @Test
    void findBookingsByBooker_Id() {
        Page<Booking> bookings = bookingRepository
                .findBookingsByBooker_Id(user2.getId(), PageRequest.of(0, 10));
        assertEquals(1, bookings.getTotalElements());
        assertEquals(booking2.getId(), bookings.getContent().get(0).getId());
    }

    @Test
    void findBookingsByItem_Owner_Id() {
        Page<Booking> bookings = bookingRepository
                .findBookingsByItem_Owner_Id(user1.getId(), PageRequest.of(0, 10));
        assertEquals(1, bookings.getTotalElements());
        assertEquals(booking1.getId(), bookings.getContent().get(0).getId());
    }

}