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
import ru.practicum.shareit.paginationvalidation.PaginationValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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

    @Mock
    private PaginationValidator paginationValidator;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User requestor;

    private Item item;

    private ItemDto itemDto;

    private ItemDtoOut itemDtoOut;

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

        itemDtoOut = new ItemDtoOut();
        itemDtoOut.setId(item.getId());
        itemDtoOut.setName(item.getName());
        itemDtoOut.setDescription(item.getDescription());
        itemDtoOut.setAvailable(item.getAvailable());
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
    void update_NameNotNull() {
        ItemDto itemDtoWithName = new ItemDto();
        itemDtoWithName.setName("New Name");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.update(requestor.getId(), item.getId(), itemDtoWithName);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(itemDtoWithName.getName(), result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_DescriptionNotNull() {
        ItemDto itemDtoWithDescription = new ItemDto();
        itemDtoWithDescription.setDescription("New Description");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.update(requestor.getId(), item.getId(), itemDtoWithDescription);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(itemDtoWithDescription.getDescription(), result.getDescription());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_AvailableNotNull() {
        ItemDto itemDtoWithAvailability = new ItemDto();
        itemDtoWithAvailability.setAvailable(false);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.update(requestor.getId(), item.getId(), itemDtoWithAvailability);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(itemDtoWithAvailability.getAvailable(), result.getAvailable());
        verify(itemRepository).save(any(Item.class));
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
    void getItem_NonOwnerViewingWithBookings() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndStatusOrderByStartAsc(any(Item.class), eq(BookingState.APPROVED)))
                .thenReturn(Collections.singletonList(booking));

        ItemDtoOut result = itemService.getItem(requestor.getId(), item.getId());

        assertNotNull(result);
        assertEquals(itemDtoOut.getId(), result.getId());
        assertNotNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getItem_NonOwnerViewingWithoutBookings() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndStatusOrderByStartAsc(any(Item.class), eq(BookingState.APPROVED)))
                .thenReturn(Collections.emptyList());

        ItemDtoOut result = itemService.getItem(requestor.getId(), item.getId());

        assertNotNull(result);
        assertEquals(itemDtoOut.getId(), result.getId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getItems_InvalidSearchParameters_FromAndSizeZero() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        doThrow(new BadRequestException(Item.class, "0", "Некорректные параметры пагинации"))
                .when(paginationValidator).validateSearchParameters(eq(0), eq(0));

        assertThrows(BadRequestException.class, () -> itemService.getItems(requestor.getId(), 0, 0));
    }

    @Test
    void getItems_InvalidSearchParameters_FromNegative() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        doThrow(new BadRequestException(Item.class, "-1", "Некорректные параметры пагинации"))
                .when(paginationValidator).validateSearchParameters(eq(-1), anyInt());

        assertThrows(BadRequestException.class, () -> itemService.getItems(requestor.getId(), -1, 10));
    }

    @Test
    void getItems_InvalidSearchParameters_SizeNegative() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        doThrow(new BadRequestException(Item.class, "-1", "Некорректные параметры пагинации"))
                .when(paginationValidator).validateSearchParameters(anyInt(), eq(-1));

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
    void search_NullText() {
        List<Item> result = itemService.search(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_BlankText() {
        List<Item> result = itemService.search("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_NoMatchingItems() {
        when(itemRepository.searchByNameAndDescription(anyString())).thenReturn(Collections.emptyList());

        List<Item> result = itemService.search("nonexistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_MatchingItems() {
        Item matchingItem1 = new Item();
        matchingItem1.setId(1);
        Item matchingItem2 = new Item();
        matchingItem2.setId(2);

        when(itemRepository.searchByNameAndDescription(anyString()))
                .thenReturn(Arrays.asList(matchingItem1, matchingItem2));

        List<Item> result = itemService.search("matching");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(matchingItem1.getId(), result.get(0).getId());
        assertEquals(matchingItem2.getId(), result.get(1).getId());
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