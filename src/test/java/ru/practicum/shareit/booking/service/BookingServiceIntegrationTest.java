package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingTimeState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.username=postgres",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceIntegrationTest {

    private final EntityManager em;

    private final BookingService service;

    @Autowired
    private ModelMapper modelMapper;


    @Test
    void add() {
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        em.flush();

        Item item = new Item(null, "item name", "item description",
                true, owner, null);
        em.persist(item);
        em.flush();

        User booker = new User(null, "booker", "booker@email.com");
        em.persist(booker);
        em.flush();

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingState.WAITING)
                .bookingTimeState(BookingTimeState.ALL)
                .build();

        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);

        Booking savedBooking = service.add(booker.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking foundBooking = query.setParameter("id", savedBooking.getId())
                .getSingleResult();

        assertNotNull(foundBooking.getId());
        assertEquals(booking.getStart(), foundBooking.getStart());
        assertEquals(booking.getEnd(), foundBooking.getEnd());
    }

    @Test
    void getBooking() {
        User user = new User(null, "username", "user@email.com");
        em.persist(user);
        em.flush();

        Item item = new Item(null, "item name", "item description",
                true, user, null);
        em.persist(item);
        em.flush();

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingState.WAITING)
                .build();
        em.persist(booking);
        em.flush();

        Booking foundBooking = service.getBooking(user.getId(), booking.getId());

        assertEquals(booking.getId(), foundBooking.getId());
        assertEquals(booking.getStart(), foundBooking.getStart());
        assertEquals(booking.getEnd(), foundBooking.getEnd());
    }

    @Test
    void getBookingsSent() {
        User user = new User(null, "username", "user@email.com");
        em.persist(user);
        em.flush();

        Item item = new Item(null, "item name", "item description",
                true, user, null);
        em.persist(item);
        em.flush();

        Booking booking1 = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingState.WAITING)
                .build();
        em.persist(booking1);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(user)
                .status(BookingState.WAITING)
                .build();
        em.persist(booking2);
        em.flush();

        List<Booking> bookings = service.getBookingsSent(user.getId(), "ALL", 0, 10);

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void getBookingsReceived() {
        User user = new User(null, "username", "user@email.com");
        em.persist(user);
        em.flush();

        User booker = new User(null, "booker", "booker@email.com");
        em.persist(booker);
        em.flush();

        Item item = new Item(null, "item name", "item description",
                true, user, null);
        em.persist(item);
        em.flush();

        Booking booking1 = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingState.WAITING)
                .build();
        em.persist(booking1);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingState.WAITING)
                .build();
        em.persist(booking2);
        em.flush();

        List<Booking> bookings = service.getBookingsReceived(user.getId(), "ALL", 0, 10);

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
    }

    @Test
    void updateBookingStatus() {
        User user = new User(null, "username", "user@email.com");
        em.persist(user);
        em.flush();

        User booker = new User(null, "booker", "booker@email.com");
        em.persist(booker);
        em.flush();

        Item item = new Item(null, "item name", "item description",
                true, user, null);
        em.persist(item);
        em.flush();

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingState.WAITING)
                .build();
        em.persist(booking);
        em.flush();

        Booking updatedBooking = service.updateBookingStatus(user.getId(), booking.getId(), true);

        assertEquals(BookingState.APPROVED, updatedBooking.getStatus());
    }

}