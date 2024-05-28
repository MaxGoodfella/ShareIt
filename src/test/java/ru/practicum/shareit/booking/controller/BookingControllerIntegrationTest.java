package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingTimeState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    LocalDateTime created = LocalDateTime.now();

    private final User user =
            new User(1, "username", "user@email.com");

    private final Item item =
            new Item(1, "item name", "item description", true, user, null);

    private final Booking booking = new Booking(1, created.plusDays(1), created.plusDays(2),
            item, user, BookingState.WAITING, BookingTimeState.ALL);

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .itemId(item.getId())
            .status(BookingState.WAITING)
            .bookingTimeState(BookingTimeState.ALL)
            .build();


    @SneakyThrows
    @Test
    void add() {
        when(bookingService.add(anyInt(), any())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(item.getId())))
                .andExpect(jsonPath("$.booker.id", is(user.getId())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.bookingTimeState").doesNotExist());

    }

    @SneakyThrows
    @Test
    void updateBookingStatus() {
        boolean approved = true;
        when(bookingService.updateBookingStatus(anyInt(), anyInt(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(item.getId())))
                .andExpect(jsonPath("$.booker.id", is(user.getId())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.bookingTimeState").doesNotExist());
    }

    @SneakyThrows
    @Test
    void getBookingByBookingId() {
        when(bookingService.getBooking(anyInt(), anyInt())).thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.item.id", is(item.getId())))
                .andExpect(jsonPath("$.booker.id", is(user.getId())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.bookingTimeState").doesNotExist());
    }

    @SneakyThrows
    @Test
    void getBookingsSentByUserId() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(bookingService.getBookingsSent(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "100")
                        .header("X-Sharer-User-Id", user.getId())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.[0].item.id", is(item.getId())))
                .andExpect(jsonPath("$.[0].booker.id", is(user.getId())))
                .andExpect(jsonPath("$.[0].status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.bookingTimeState").doesNotExist());
    }

    @SneakyThrows
    @Test
    void getBookingsReceivedByUserId() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(bookingService.getBookingsReceived(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "100")
                        .header("X-Sharer-User-Id", user.getId())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.[0].item.id", is(item.getId())))
                .andExpect(jsonPath("$.[0].booker.id", is(user.getId())))
                .andExpect(jsonPath("$.[0].status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.bookingTimeState").doesNotExist());
    }

}