package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private JpaBookingRepository bookingRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaItemRepository itemRepository;

    @Mock
    private JpaCommentRepository commentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User requestor;

    private Item item;

    private ItemDto itemDto;

    private Comment comment;

    private Booking booking;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setId(1);
        requestor.setName("user name");

        item = new Item();
        item.setId(1);
        item.setName("item name");
        item.setDescription("item description");
        item.setOwner(requestor);

        itemDto = new ItemDto();
        itemDto.setName("item name");
        itemDto.setDescription("item description");
        itemDto.setAvailable(true);

        comment = new Comment();
        comment.setId(1);
        comment.setText("comment");
        comment.setAuthor(requestor);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(requestor);
        booking.setStatus(BookingState.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
    }


    @Test
    void add_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findByNameAndDescription(anyString(), anyString())).thenReturn(Optional.empty());
        when(modelMapper.map(any(ItemDto.class), eq(Item.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.add(requestor.getId(), itemDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void add_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.add(requestor.getId(), itemDto));
    }

    @Test
    void add_ItemAlreadyExists() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findByNameAndDescription(anyString(), anyString())).thenReturn(Optional.of(item));
        when(modelMapper.map(any(ItemDto.class), eq(Item.class))).thenReturn(item);

        assertThrows(EntityAlreadyExistsException.class, () -> itemService.add(requestor.getId(), itemDto));
    }

    @Test
    void update_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.update(requestor.getId(), item.getId(), itemDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(requestor.getId(), item.getId(), itemDto));
    }

    @Test
    void update_ItemNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(requestor.getId(), item.getId(), itemDto));
    }

    @Test
    void update_AccessDenied() {
        User anotherUser = new User();
        anotherUser.setId(2);
        item.setOwner(anotherUser);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> itemService.update(requestor.getId(), item.getId(), itemDto));
    }

    @Test
    void getItem_Success() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndStatusOrderByStartAsc(any(Item.class), eq(BookingState.APPROVED)))
                .thenReturn(Collections.singletonList(booking));

        ItemDtoOut itemDtoOut = new ItemDtoOut(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable());
        itemDtoOut.setComments(Collections.emptyList());

        ItemDtoOut result = itemService.getItem(requestor.getId(), item.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void getItem_ItemNotFound() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(requestor.getId(), item.getId()));
    }

    @Test
    void getItems_InvalidSearchParameters_FromAndSizeZero() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));

        assertThrows(BadRequestException.class, () -> itemService.getItems(requestor.getId(), 0, 0));
    }

    @Test
    void getItems_InvalidSearchParameters_FromNegative() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));

        assertThrows(BadRequestException.class, () -> itemService.getItems(requestor.getId(), -1, 10));
    }

    @Test
    void getItems_InvalidSearchParameters_SizeNegative() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));

        assertThrows(BadRequestException.class, () -> itemService.getItems(requestor.getId(), 0, -1));
    }

    @Test
    void getItems_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findByOwnerId(anyInt(), any(Pageable.class))).thenReturn(Collections.singletonList(item));
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingState.class)))
                .thenReturn(Collections.emptyList());

        List<ItemDtoOut> result = itemService.getItems(requestor.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        verify(userRepository).findById(anyInt());
        verify(itemRepository).findByOwnerId(anyInt(), any(Pageable.class));
    }

    @Test
    void getItems_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItems(requestor.getId(), 0, 10));
    }

    @Test
    void search_Success() {
        when(itemRepository.searchByNameAndDescription(anyString())).thenReturn(Collections.singletonList(item));

        List<Item> result = itemService.search("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
    }

    @Test
    void search_EmptyText() {
        List<Item> result = itemService.search("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_Success() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerId(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        ItemDto.ItemCommentDto commentDto = new ItemDto.ItemCommentDto();
        commentDto.setText("Test Comment");

        ItemDto.ItemCommentDto result = itemService.addComment(requestor.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
    }

    @Test
    void addComment_UserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        ItemDto.ItemCommentDto commentDto = new ItemDto.ItemCommentDto();
        commentDto.setText("Test Comment");

        assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(requestor.getId(), item.getId(), commentDto));
    }

    @Test
    void addComment_ItemNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        ItemDto.ItemCommentDto commentDto = new ItemDto.ItemCommentDto();
        commentDto.setText("Test Comment");

        assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(requestor.getId(), item.getId(), commentDto));
    }

    @Test
    void addComment_CommentTextEmpty() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        ItemDto.ItemCommentDto commentDto = new ItemDto.ItemCommentDto();
        commentDto.setText("");

        assertThrows(IllegalArgumentException.class,
                () -> itemService.addComment(requestor.getId(), item.getId(), commentDto));
    }

    @Test
    void addComment_NoApprovedBooking() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerId(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        ItemDto.ItemCommentDto commentDto = new ItemDto.ItemCommentDto();
        commentDto.setText("Test Comment");

        assertThrows(BadRequestException.class,
                () -> itemService.addComment(requestor.getId(), item.getId(), commentDto));
    }

}