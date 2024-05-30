package ru.practicum.shareit.booking.controller;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;


    @PostMapping
    public Booking add(@RequestHeader(REQUEST_HEADER) Integer userId,
                       @Valid @RequestBody BookingDto bookingDto) {
        log.info("Start saving booking {}", bookingDto);
        Booking addedBooking = bookingService.add(userId, bookingDto);
        log.info("Finish saving booking {}", addedBooking);
        return addedBooking;
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBookingStatus(@RequestHeader(REQUEST_HEADER) Integer userId,
                                       @PathVariable("bookingId") Integer bookingId,
                                       @RequestParam("approved") boolean approved) {
        log.info("Start updating booking status for bookingId {}", bookingId);
        Booking updatedBooking = bookingService.updateBookingStatus(userId, bookingId, approved);
        log.info("Finish updating booking status for bookingId {}", bookingId);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingByBookingId(@RequestHeader(REQUEST_HEADER) Integer userId,
                                         @PathVariable Integer bookingId) {
        log.info("Start fetching booking with id = {}", bookingId);
        Booking fetchedBooking = bookingService.getBooking(userId, bookingId);
        log.info("Finish fetching booking with id = {}", fetchedBooking.getId());
        return fetchedBooking;
    }

    @GetMapping
    public List<Booking> getBookingsSentByUserId(@RequestHeader(REQUEST_HEADER) Integer userId,
                                                 @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @RequestParam(required = false, defaultValue = "100") Integer size) {
        log.info("Start fetching bookings with state '{}' from user with id = {}", userId, state);
        List<Booking> fetchedBookings = bookingService.getBookingsSent(userId, state.toUpperCase(), from, size);
        log.info("Finish fetching bookings with state '{}' from user with id = {}", userId, state);
        return fetchedBookings;

    }

    @GetMapping("/owner")
    public List<Booking> getBookingsReceivedByUserId(@RequestHeader(REQUEST_HEADER) Integer userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(required = false, defaultValue = "100") Integer size) {
        log.info("Start fetching bookings with state '{}' for user with id = {}", userId, state);
        List<Booking> fetchedBookings = bookingService.getBookingsReceived(userId, state.toUpperCase(), from, size);
        log.info("Finish fetching bookings with state '{}' for user with id = {}", userId, state);
        return fetchedBookings;
    }

}