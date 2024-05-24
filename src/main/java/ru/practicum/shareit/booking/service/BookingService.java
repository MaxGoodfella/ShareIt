package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking add(Integer userId, BookingDto bookingDto);

    Booking getBooking(Integer userId, Integer bookingId);

    List<Booking> getBookingsSent(Integer userId, String state, Integer from, Integer size);

    List<Booking> getBookingsReceived(Integer userId, String state, Integer from, Integer size);

    Booking updateBookingStatus(Integer userId, Integer bookingId, boolean approved);

}