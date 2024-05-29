package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingTimeState;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final JpaBookingRepository bookingRepository;

    private final JpaUserRepository userRepository;

    private final JpaItemRepository itemRepository;

    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public Booking add(Integer userId, BookingDto bookingDto) {

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IllegalArgumentException("Start/end time cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(Item.class, String.valueOf(bookingDto.getItemId()),
                        "Предмет с id " + bookingDto.getItemId() + " не найден."));

        Integer ownerId = item.getOwner().getId();

        if (ownerId != null) {
            if (ownerId.equals(userId)) {
                throw new EntityNotFoundException(Integer.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не может забронировать свой же предмет");
            }
        }

        if (!item.getAvailable()) {
            throw new BadRequestException(Item.class, String.valueOf(item.getId()),
                    "Предмет с id = " + item.getId() + " недоступен для бронирования");
        }

        Booking newBooking = modelMapper.map(bookingDto, Booking.class);

        newBooking.setBooker(user);
        newBooking.setItem(item);
        newBooking.setStatus(BookingState.WAITING);
        newBooking.setBookingTimeState(BookingTimeState.ALL);

        return bookingRepository.save(newBooking);

    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Integer userId, Integer bookingId, boolean approved) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(Booking.class, String.valueOf(bookingId),
                        "Бронирование с id " + bookingId + " не найдено."));

        Item item = booking.getItem();

        Integer ownerId = item.getOwner().getId();

        if (ownerId != null) {
            if (!ownerId.equals(userId)) {
                throw new EntityNotFoundException(Integer.class, String.valueOf(userId),
                        "Пользователь с id = " + userId + " не имеет права подтверждать/отклонять данное бронирование.");
            }
        }

        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new BadRequestException(Item.class, String.valueOf(bookingId),
                    "Бронирование с id = " + bookingId + " уже подтверждено пользователем с id = " + userId);
        }

        if (approved) {
            booking.setStatus(BookingState.APPROVED);
        } else {
            booking.setStatus(BookingState.REJECTED);
        }

        return bookingRepository.save(booking);

    }

    @Override
    public Booking getBooking(Integer userId, Integer bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(Booking.class, String.valueOf(bookingId),
                        "Бронирование с id " + bookingId + " не найдено."));

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Integer bookerId = booking.getBooker().getId();
        Integer ownerId = booking.getItem().getOwner().getId();

        if (bookerId != null && ownerId != null) {
            if (!(bookerId.equals(userId) || ownerId.equals(userId))) {
                throw new EntityNotFoundException(Booking.class, String.valueOf(bookingId),
                        "Бронирование с id " + bookingId + " не найдено.");
            }
        }

        return booking;

    }

    @Override
    public List<Booking> getBookingsSent(Integer userId, String state, Integer from, Integer size) {

        validateSearchParameters(from, size);

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        Page<Booking> bookingPage = bookingRepository.findBookingsByBooker_Id(userId, pageable);
        return filterBookingsByState(bookingPage.getContent(), state);

    }

    @Override
    public List<Booking> getBookingsReceived(Integer userId, String state, Integer from, Integer size) {

        validateSearchParameters(from, size);

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        Page<Booking> bookingPage = bookingRepository.findBookingsByItem_Owner_Id(userId, pageable);
        return filterBookingsByState(bookingPage.getContent(), state);

    }


    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {

        validateState(state);

        List<Booking> filteredBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookings) {
            switch (state) {
                case "CURRENT":
                    filterCurrentBookings(booking, now, filteredBookings);
                    break;
                case "PAST":
                    filterPastBookings(booking, now, filteredBookings);
                    break;
                case "FUTURE":
                    filterFutureBookings(booking, now, filteredBookings);
                    break;
                case "":
                case "ALL":
                    filteredBookings.add(booking);
                    break;
                default:
                    if (booking.getStatus() != null) {
                        switch (booking.getStatus()) {
                            case WAITING:
                            case APPROVED:
                            case REJECTED:
                            case CANCELED:
                                if (booking.getStatus().name().equalsIgnoreCase(state)) {
                                    filteredBookings.add(booking);
                                }
                                break;
                        }
                    }
                    break;
            }
        }

        filteredBookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return filteredBookings;

    }

    private void filterCurrentBookings(Booking booking, LocalDateTime now, List<Booking> filteredBookings) {
        if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
            booking.setBookingTimeState(BookingTimeState.CURRENT);
            filteredBookings.add(booking);
        }
    }

    private void filterPastBookings(Booking booking, LocalDateTime now, List<Booking> filteredBookings) {
        if (booking.getEnd().isBefore(now)) {
            booking.setBookingTimeState(BookingTimeState.PAST);
            filteredBookings.add(booking);
        }
    }

    private void filterFutureBookings(Booking booking, LocalDateTime now, List<Booking> filteredBookings) {
        if (booking.getStart().isAfter(now)) {
            booking.setBookingTimeState(BookingTimeState.FUTURE);
            filteredBookings.add(booking);
        }
    }

    private void validateState(String state) {
        if (!(state.equalsIgnoreCase("WAITING")
                || state.equalsIgnoreCase("APPROVED")
                || state.equalsIgnoreCase("REJECTED")
                || state.equalsIgnoreCase("CANCELED")
                || state.equalsIgnoreCase("ALL")
                || state.equalsIgnoreCase("CURRENT")
                || state.equalsIgnoreCase("PAST")
                || state.equalsIgnoreCase("FUTURE")
                || state.equalsIgnoreCase(""))) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    private void validateSearchParameters(int from, int size) {
        if (from == 0 && size == 0) {
            throw new BadRequestException(Integer.class, from + " & " + size,
                    "Некорректные параметры поиска: from = " + from + " и " + " size = " + size);
        }

        if (from < 0 || size <= 0) {
            throw new BadRequestException(Integer.class, from + " & " + size,
                    "Некорректные параметры поиска: from = " + from + " и " + " size = " + size);
        }
    }

}