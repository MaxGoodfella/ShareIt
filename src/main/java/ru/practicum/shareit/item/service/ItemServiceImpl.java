package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository itemRepository;

    private final JpaUserRepository userRepository;

    private final JpaBookingRepository bookingRepository;

    private final JpaCommentRepository commentRepository;

    private final ModelMapper modelMapper;


    @Override
    public Item add(Integer userId, ItemDto itemDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Item newItem = modelMapper.map(itemDto, Item.class);

        if (itemRepository.findByNameAndDescription(newItem.getName(), newItem.getDescription()).isPresent()) {
            throw new EntityAlreadyExistsException(Item.class,
                    "Item с названием '" + newItem.getName() +
                            "' и описанием '" + newItem.getDescription() +
                            "' уже зарегистрирован.");
        }

        newItem.setOwner(user);

        return itemRepository.save(newItem);
    }

    @Override
    public Item update(Integer userId, Integer itemId, ItemDto itemDto) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class, String.valueOf(itemId),
                        "Вещь с id " + itemId + " не найдена."));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(Integer.class, String.valueOf(userId),
                    "Пользователь с id = " + userId + " не имеет права обновлять эту вещь.");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(existingItem.getAvailable())) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.save(existingItem);

    }

    @Override
    public List<ItemDtoOut> getItems(Integer userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        List<Item> itemList = itemRepository.findByOwnerId(userId);
        List<Integer> idList = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Integer, List<CommentDtoOut>> comments = commentRepository.findAllByItemIdIn(idList)
                .stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(groupingBy(CommentDtoOut::getItemId, toList()));

        Map<Integer, List<BookingDtoOut>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(itemList,
                        BookingState.APPROVED)
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(groupingBy(BookingDtoOut::getItemId, toList()));

        return itemList.stream()
                .sorted(Comparator.comparingInt(Item::getId))
                .map(item -> ItemMapper.toItemDtoOut(
                        item,
                        getLastBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())
                ))
                .collect(Collectors.toList());

    }


    @Override
    public List<Item> search(String text) {

        if (text == null || text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByNameAndDescription(text.toLowerCase());

    }

    @Override
    public ItemDto.ItemCommentDto addComment(Integer userId, Integer itemId, ItemDto.ItemCommentDto commentDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.valueOf(userId),
                        "Пользователь с id " + userId + " не найден."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class, String.valueOf(itemId),
                        "Вещь с id " + itemId + " не найдена."));

        if (commentDto.getText() == null || commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }


        List<Booking> userBookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);
        boolean hasApprovedBooking = false;

        commentDto.setCreated(LocalDateTime.now());

        for (Booking booking : userBookings) {
            if (booking.getStatus() == BookingState.APPROVED &&
                    booking.getEnd().isBefore(commentDto.getCreated())) {
                hasApprovedBooking = true;
                break;
            }
        }

        if (!hasApprovedBooking) {
            throw new BadRequestException(Item.class, String.valueOf(itemId),
                    "Пользователь не брал вещь c id = " + itemId + " в аренду или аренда еще не завершена.");
        }

        Comment comment = CommentMapper.mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment = commentRepository.save(comment);

        return CommentMapper.mapToItemCommentDto(comment);

    }

    @Override
    public ItemDtoOut getItem(Integer userId, Integer itemId) {

        Optional<Item> itemGet = itemRepository.findById(itemId);
        if (itemGet.isEmpty()) {
            throw new EntityNotFoundException(Item.class, String.valueOf(itemId),
                    "Вещь с id " + itemId + " не найдена.");
        }

        Item item = itemGet.get();
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
        itemDtoOut.setComments(getAllItemComments(itemId));

        if (!item.getOwner().getId().equals(userId)) {
            return itemDtoOut;
        }

        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(item, BookingState.APPROVED);
        List<BookingDtoOut> bookingDTOList = bookings
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(toList());

        itemDtoOut.setLastBooking(getLastBooking(bookingDTOList, LocalDateTime.now()));
        itemDtoOut.setNextBooking(getNextBooking(bookingDTOList, LocalDateTime.now()));
        return itemDtoOut;

    }


    private List<CommentDtoOut> getAllItemComments(Integer itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList());
    }


    private BookingDtoOut getLastBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> !bookingDTO.getStart().isAfter(time))
                .reduce((booking1, booking2) -> booking1.getStart().isAfter(booking2.getStart()) ? booking1 : booking2)
                .orElse(null);
    }

    private BookingDtoOut getNextBooking(List<BookingDtoOut> bookings, LocalDateTime time) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> bookingDTO.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }

}