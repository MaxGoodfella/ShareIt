package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private JpaBookingRepository bookingRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaItemRepository itemRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;

    private User booker;

    private Item item;

    private Booking booking;

    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setId(1);

        booker = new User();
        booker.setId(2);

        item = new Item();
        item.setId(1);
        item.setOwner(owner);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(1);
    }


    @Test
    public void testAdd_NullStartOrEnd() {
        bookingDto.setStart(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.add(booker.getId(), bookingDto));
        assertEquals("Start/end time cannot be null", exception.getMessage());

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(null);
        exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.add(booker.getId(), bookingDto));
        assertEquals("Start/end time cannot be null", exception.getMessage());
    }

    @Test
    public void testAdd_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.add(booker.getId(), bookingDto));
    }

    @Test
    public void testAdd_ItemNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.add(booker.getId(), bookingDto));
    }

    @Test
    public void testAdd_UserBookingOwnItem() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> bookingService.add(owner.getId(), bookingDto));
    }

    @Test
    public void testAdd_ItemNotAvailable() {
        item.setAvailable(false);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.add(booker.getId(), bookingDto));
    }

    @Test
    public void testAdd_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(modelMapper.map(any(BookingDto.class), any(Class.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.add(booker.getId(), bookingDto);

        assertNotNull(result);
        assertEquals(booking, result);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void testUpdateBookingStatus_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateBookingStatus(owner.getId(), booking.getId(), true));
    }

    @Test
    public void testUpdateBookingStatus_BookingNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateBookingStatus(owner.getId(), booking.getId(), true));
    }

    @Test
    public void testUpdateBookingStatus_UserNotOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.updateBookingStatus(booker.getId(), booking.getId(), true));
    }

    @Test
    public void testUpdateBookingStatus_BookingAlreadyApproved() {
        booking.setStatus(BookingState.APPROVED);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class,
                () -> bookingService.updateBookingStatus(owner.getId(), booking.getId(), true));
    }

    @Test
    public void testUpdateBookingStatus_Approved() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.updateBookingStatus(owner.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(BookingState.APPROVED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void testUpdateBookingStatus_Rejected() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.updateBookingStatus(owner.getId(), booking.getId(), false);

        assertNotNull(result);
        assertEquals(BookingState.REJECTED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void testGetBooking() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));

        Booking result = bookingService.getBooking(booker.getId(), booking.getId());

        assertEquals(booking, result);
    }

    @Test
    public void testGetBooking_BookingNotFound() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(booker.getId(), booking.getId()));
    }

    @Test
    public void testGetBooking_UserNotFound() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(booker.getId(), booking.getId()));
    }

    @Test
    public void testGetBookingsSent() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBooker_Id(anyInt(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<Booking> result = bookingService.getBookingsSent(booker.getId(), "ALL", 0, 10);

        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    public void testGetBookingsSent_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingsSent(booker.getId(), "ALL", 0, 10));
    }

    @Test
    public void testGetBookingsReceived() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(bookingRepository.findBookingsByItem_Owner_Id(anyInt(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<Booking> result = bookingService.getBookingsReceived(owner.getId(), "ALL", 0, 10);

        assertEquals(1, result.size());
        assertEquals(booking, result.get(0));
    }

    @Test
    public void testGetBookingsReceived_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingsReceived(owner.getId(), "ALL", 0, 10));
    }

}