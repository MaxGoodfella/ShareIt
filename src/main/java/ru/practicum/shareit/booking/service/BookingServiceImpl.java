package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingTimeState;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
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
public class BookingServiceImpl implements BookingService {

    private final JpaBookingRepository bookingRepository;
    private final JpaUserRepository userRepository;
    private final JpaItemRepository itemRepository;
    private final ModelMapper modelMapper;

    @Override
    public Booking add(Integer userId, BookingDto bookingDto) {

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IllegalArgumentException("Start/end time cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(Item.class,
                        "Предмет с id " + bookingDto.getItemId() + " не найден."));


        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(Integer.class,
                    "Пользователь с id = " + userId + " не может забронировать свой же предмет");
        }


        if (!item.getAvailable()) {
            throw new BadRequestException(Item.class,
                    "Предмет с id = " + item.getId() + " недоступен для бронирования");
        }

        Booking newBooking = modelMapper.map(bookingDto, Booking.class);

        newBooking.setBooker(user);
        newBooking.setItem(item);
        newBooking.setStatus(BookingState.WAITING);
        newBooking.setBookingTimeState(BookingTimeState.ALL); // ???

        return bookingRepository.save(newBooking);

    }

    @Override
    public Booking updateBookingStatus(Integer userId, Integer bookingId, boolean approved) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));


        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(Booking.class,
                        "Бронирование с id " + bookingId + " не найдено."));

        Item item = booking.getItem();

        // TODO: не очень

        if (!item.getOwner().getId().equals(userId)) {
//            throw new AccessDeniedException(Integer.class,
//                    "Пользователь с id = " + userId + " не имеет права подтверждать/отклонять данное бронирование.");
            throw new EntityNotFoundException(Integer.class,
                    "Пользователь с id = " + userId + " не имеет права подтверждать/отклонять данное бронирование.");
        }

        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new BadRequestException(Item.class,
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
                .orElseThrow(() -> new EntityNotFoundException(Booking.class,
                        "Бронирование с id " + bookingId + " не найдено."));


        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));


        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
//            throw new AccessDeniedException(Integer.class,
//                    "Пользователь с id = " + userId + " не имеет права просматривать данное бронирование.");
            throw new EntityNotFoundException(Booking.class,
                    "Бронирование с id " + bookingId + " не найдено.");
        }

        return booking;

    }

    @Override
    public List<Booking> getBookingsSent(Integer userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        List<Booking> bookings = bookingRepository.findBookingsByBooker_Id(userId);
        return filterBookingsByState(bookings, state);

//        List<Booking> filteredBookings = new ArrayList<>();
//
//        LocalDateTime now = LocalDateTime.now();
//
//        for (Booking booking : bookings) {
//            switch (state) {
//                case "CURRENT":
//                    if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
//                        booking.setBookingTimeState(BookingTimeState.CURRENT);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "PAST":
//                    if (booking.getEnd().isBefore(now)) {
//                        booking.setBookingTimeState(BookingTimeState.PAST);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "FUTURE":
//                    if (booking.getStart().isAfter(now)) {
//                        booking.setBookingTimeState(BookingTimeState.FUTURE);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "":
//                case "ALL":
//                    filteredBookings.add(booking);
//                    break;
//                default:
//                    if (booking.getStatus() != null) {
//                        switch (booking.getStatus()) {
//                            case WAITING:
//                            case APPROVED:
//                            case REJECTED:
//                            case CANCELED:
//                                if (booking.getStatus().name().equalsIgnoreCase(state)) {
//                                    filteredBookings.add(booking);
//                                }
//                                // break;
//                            default:
//                                throw new IllegalArgumentException("Unknown state: " + state);
//                                // break;
//                        }
//                    }
//                    break;
//            }
//        }
//
//        filteredBookings.sort(Comparator.comparing(Booking::getStart).reversed());
//
//        return filteredBookings;
    }



    @Override
    public List<Booking> getBookingsReceived(Integer userId, String state) {
       userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        List<Booking> bookings = bookingRepository.findBookingsByItem_Owner_Id(userId);
        return filterBookingsByState(bookings, state);

//        List<Booking> filteredBookings = new ArrayList<>();
//
//        LocalDateTime now = LocalDateTime.now();
//
//        for (Booking booking : bookings) {
//            switch (state) {
//                case "CURRENT":
//                    if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
//                        booking.setBookingTimeState(BookingTimeState.CURRENT);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "PAST":
//                    if (booking.getEnd().isBefore(now)) {
//                        booking.setBookingTimeState(BookingTimeState.PAST);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "FUTURE":
//                    if (booking.getStart().isAfter(now)) {
//                        booking.setBookingTimeState(BookingTimeState.FUTURE);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "":
//                case "ALL":
//                    filteredBookings.add(booking);
//                    break;
//                default:
//                    if (booking.getStatus() != null) {
//                        switch (booking.getStatus()) {
//                            case WAITING:
//                            case APPROVED:
//                            case REJECTED:
//                            case CANCELED:
//                                if (booking.getStatus().name().equalsIgnoreCase(state)) {
//                                    filteredBookings.add(booking);
//                                }
//                                // break;
//                            default:
//                                throw new IllegalArgumentException("Unknown state: " + state);
//                                // break;
//                        }
//                    }
//                    break;
//            }
//        }
//
//        filteredBookings.sort(Comparator.comparing(Booking::getStart).reversed());
//
//        return filteredBookings;
    }



//    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
//        List<Booking> filteredBookings = new ArrayList<>();
//        LocalDateTime now = LocalDateTime.now();
//
//        for (Booking booking : bookings) {
//            switch (state) {
//                case "CURRENT":
//                    if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
//                        booking.setBookingTimeState(BookingTimeState.CURRENT);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "PAST":
//                    if (booking.getEnd().isBefore(now)) {
//                        booking.setBookingTimeState(BookingTimeState.PAST);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "FUTURE":
//                    if (booking.getStart().isAfter(now)) {
//                        booking.setBookingTimeState(BookingTimeState.FUTURE);
//                        filteredBookings.add(booking);
//                    }
//                    break;
//                case "":
//                case "ALL":
//                    filteredBookings.add(booking);
//                    break;
//                default:
//                    if (booking.getStatus() != null) {
//                        switch (booking.getStatus()) {
//                            case WAITING:
//                            case APPROVED:
//                            case REJECTED:
//                            case CANCELED:
//                                if (booking.getStatus().name().equalsIgnoreCase(state)) {
//                                    filteredBookings.add(booking);
//                                }
//                                break;
//                            default:
//                                throw new IllegalArgumentException("Unknown state: " + state);
//                        }
//                    }
//                    break;
//            }
//        }
//
//        filteredBookings.sort(Comparator.comparing(Booking::getStart).reversed());
//        return filteredBookings;
//    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {


        List<Booking> filteredBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookings) {
            switch (state) {
                case "CURRENT":
                    if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now)) {
                        booking.setBookingTimeState(BookingTimeState.CURRENT);
                        filteredBookings.add(booking);
                    }
                    break;
                case "PAST":
                    if (booking.getEnd().isBefore(now)) {
                        booking.setBookingTimeState(BookingTimeState.PAST);
                        filteredBookings.add(booking);
                    }
                    break;
                case "FUTURE":
                    if (booking.getStart().isAfter(now)) {
                        booking.setBookingTimeState(BookingTimeState.FUTURE);
                        filteredBookings.add(booking);
                    }
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
                            default:
                                throw new IllegalArgumentException("Unsupported state: " + state);
                        }
                    }
                    break;
            }
        }

        filteredBookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return filteredBookings;
    }


}