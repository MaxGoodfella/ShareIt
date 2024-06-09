package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.Valid;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(REQUEST_HEADER) Long userId,
                                      @Valid @RequestBody BookItemRequestDto bookingDto) {
        log.info("Start saving booking {}", bookingDto);
        ResponseEntity<Object> response = bookingClient.bookItem(userId, bookingDto);
        log.info("Finish saving booking {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(REQUEST_HEADER) Long userId,
                                                      @PathVariable("bookingId") Long bookingId,
                                                      @RequestParam("approved") boolean approved) {
        log.info("Start updating booking status for bookingId {}", bookingId);
        ResponseEntity<Object> response = bookingClient.updateBookingStatus(userId, bookingId, approved);
        log.info("Finish updating booking status for bookingId {}", response);
        return response;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByBookingId(@RequestHeader(REQUEST_HEADER) Long userId,
                                                        @PathVariable Long bookingId) {
        log.info("Start fetching booking with id = {}", bookingId);
        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);
        log.info("Finish fetching booking with id = {}", bookingId);
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsSentByUserId(@RequestHeader(REQUEST_HEADER) Long userId,
                                                          @RequestParam(value = "state",
                                                                  defaultValue = "ALL") String state,
                                                          @RequestParam(required = false,
                                                                  defaultValue = "0") Long from,
                                                          @RequestParam(required = false,
                                                                  defaultValue = "100") Long size) {
        log.info("Start fetching bookings with state '{}' from user with id = {}", state, userId);
        ResponseEntity<Object> response = bookingClient.getBookingsSent(userId, state.toUpperCase(), from, size);
        log.info("Finish fetching bookings with state '{}' from user with id = {}", state, userId);
        return response;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsReceivedByUserId(@RequestHeader(REQUEST_HEADER) Long userId,
                                                              @RequestParam(value = "state",
                                                                      defaultValue = "ALL") String state,
                                                              @RequestParam(required = false,
                                                                      defaultValue = "0") Long from,
                                                              @RequestParam(required = false,
                                                                      defaultValue = "100") Long size) {
        log.info("Start fetching bookings with state '{}' for user with id = {}", state, userId);
        ResponseEntity<Object> response = bookingClient.getBookingsReceived(userId, state.toUpperCase(), from, size);
        log.info("Finish fetching bookings with state '{}' for user with id = {}", state, userId);
        return response;
    }

}