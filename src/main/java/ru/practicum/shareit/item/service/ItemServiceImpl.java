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
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
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
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        Item newItem = modelMapper.map(itemDto, Item.class);

        if (itemRepository.findByNameAndDescription(newItem.getName(), newItem.getDescription()).isPresent()) {
            throw new EntityAlreadyExistsException(Item.class, "Item с названием '" +
                    newItem.getName() + "' и описанием '" + newItem.getDescription() + "' уже зарегистрирован.");
        }

        newItem.setOwner(user);

        return itemRepository.save(newItem);
    }

    @Override
    public Item update(Integer userId, Integer itemId, ItemDto itemDto) {

        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class,
                        "Вещь с id " + itemId + " не найдена."));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(Integer.class,
                    "Пользователь с id = " + userId +  " не имеет права обновлять эту вещь.");
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


//    @Override
//    public ItemDto getItem(Integer userId, Integer itemId) {
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new EntityNotFoundException(Item.class,
//                        "Вещь с id " + itemId + " не найдена."));
//
//        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
//
//        List<ItemDto.ItemCommentDto> commentDtos = getCommentDtosForItem(itemId);
//        itemDto.setComments(commentDtos);
//
//        setLastAndNextBooking(itemDto, item.getId());
//
//        return itemDto;
//    }

    // старый

//    @Override
//    public List<ItemDto> getItems(Integer userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException(User.class,
//                        "Пользователь с id " + userId + " не найден."));
//
//        List<Item> items = itemRepository.findByOwnerId(userId);
//        List<ItemDto> itemDtos = new ArrayList<>();
//
//        for (Item item : items) {
//            ItemDto itemDto = modelMapper.map(item, ItemDto.class);
//
//            List<ItemDto.ItemCommentDto> commentDtos = getCommentDtosForItem(item.getId());
//            itemDto.setComments(commentDtos);
//
//            setLastAndNextBooking(itemDto, item.getId());
//
//            itemDtos.add(itemDto);
//        }
//
//        return itemDtos;
//    }


    @Override
    public List<ItemDtoOut> getItems(Integer userId) {

//        UserDto owner = userService.findById(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
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

        return itemList
                .stream()
                .map(item -> ItemMapper.toItemDtoOut(
                        item,
                        getLastBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())
                ))
                .collect(toList());
    }


    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByNameAndDescription(text.toLowerCase());

        // return itemRepository.findByAvailableIsTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
    }

    @Override
    public ItemDto.ItemCommentDto addComment(Integer userId, Integer itemId, ItemDto.ItemCommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class,
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
            throw new BadRequestException(Item.class,
                    "Пользователь не брал вещь c id = " + itemId + " в аренду или аренда еще не завершена.");
        }


        Comment comment = mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment = commentRepository.save(comment);

        return mapToItemCommentDto(comment);
    }


    //    @Override
//    public ItemDto getItem(Integer userId, Integer itemId) {
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new EntityNotFoundException(Item.class,
//                        "Вещь с id " + itemId + " не найдена."));
//
//        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
//
//        List<ItemDto.ItemCommentDto> commentDtos = getCommentDtosForItem(itemId);
//        itemDto.setComments(commentDtos);
//
//        setLastAndNextBooking(itemDto, item.getId());
//
//        return itemDto;
//    }

    @Override
    public ItemDtoOut getItem(Integer userId, Integer itemId) {


//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new EntityNotFoundException(Item.class,
//                        "Вещь с id " + itemId + " не найдена."));
//
//
//
//        ItemDto itemDto = new ItemDto();
//        // List<Booking> bookings = bookingRepository.findBookingsByItem_Id(itemId);
//        List<ItemDto.ItemCommentDto> commentDtos = getCommentDtosForItem(itemId);
//        itemDto.setComments(commentDtos);


//        if (item.getOwner().getId().equals(userId) && !bookings.isEmpty()) {
//            Booking lastBooking = bookings.stream()
//                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
//                            !booking.getStatus().equals(BookingState.REJECTED))
//                    .min(Booking::compareTo)
//                    .orElse(null);
//
//            Booking nextBooking = bookings.stream()
//                    .filter((booking) -> booking.getStart().isAfter(LocalDateTime.now()) &&
//                            !booking.getStatus().equals(BookingState.REJECTED))
//                    .max(Booking::compareTo)
//                    .orElse(null);
//
//            itemDto = toDto(item, lastBooking == null ? null : BookingDtoMapper.toDto(lastBooking),
//                                  nextBooking == null ? null : BookingDtoMapper.toDto(nextBooking), commentsDtos);
//        } else {
//            itemDto = toDto(item, null, null, commentsDtos);
//        }
//
//        log.info("Found item: " + item);
//
//        return itemDto;


        // userService.findById(userId);
        Optional<Item> itemGet = itemRepository.findById(itemId);
        if (itemGet.isEmpty()) {
            throw new EntityNotFoundException(Item.class,
                        "Вещь с id " + itemId + " не найдена.");
        }


        Item item = itemGet.get();
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(itemGet.get());
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


    public List<CommentDtoOut> getAllItemComments(Integer itemId) {
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


        private ItemDto.ItemBookingDto mapToItemBookingDto(Booking booking) {
        return new ItemDto.ItemBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId()
        );
    }

    private ItemDto.ItemCommentDto mapToItemCommentDto(Comment comment) {
        return new ItemDto.ItemCommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    private Comment mapToComment(ItemDto.ItemCommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }


}





//
//    private List<ItemDto.ItemCommentDto> getCommentDtosForItem(Integer itemId) {
//        List<Comment> comments = commentRepository.findAllByItemId(itemId);
//        List<ItemDto.ItemCommentDto> commentDtos = new ArrayList<>();
//        for (Comment comment : comments) {
//            commentDtos.add(mapToItemCommentDto(comment));
//        }
//        return commentDtos;
//    }
//
//    private void setLastAndNextBooking(ItemDto itemDto, Integer itemId) {
//        List<Booking> bookings = bookingRepository.findBookingsByItem_Owner_Id(itemId);
//        if (!bookings.isEmpty()) {
//            List<Booking> pastBookings = new ArrayList<>();
//            List<Booking> futureBookings = new ArrayList<>();
//            LocalDateTime now = LocalDateTime.now();
//
//            for (Booking booking : bookings) {
//                if (booking.getEnd().isBefore(now)) {
//                    pastBookings.add(booking);
//                } else {
//                    futureBookings.add(booking);
//                }
//            }
//
////            pastBookings.sort(Comparator.comparing(Booking::getEnd).reversed());
////            futureBookings.sort(Comparator.comparing(Booking::getStart));
//
//
//            pastBookings.sort(Comparator.comparing(Booking::getEnd));
//            futureBookings.sort(Comparator.comparing(Booking::getStart));
//
//            Booking closestPastBooking = pastBookings.isEmpty() ? null : pastBookings.get(0);
//            Booking closestFutureBooking = futureBookings.isEmpty() ? null : futureBookings.get(0);
//
//            if (closestPastBooking != null) {
//                itemDto.setLastBooking(mapToItemBookingDto(closestPastBooking));
//            }
//            if (closestFutureBooking != null) {
//                itemDto.setNextBooking(mapToItemBookingDto(closestFutureBooking));
//            }
//        }
//    }




//    private ItemDto.ItemBookingDto mapToItemBookingDto(Booking booking) {
//        return new ItemDto.ItemBookingDto(
//                booking.getId(),
//                booking.getStart(),
//                booking.getEnd(),
//                booking.getBooker().getId()
//        );
//    }
//
//    private ItemDto.ItemCommentDto mapToItemCommentDto(Comment comment) {
//        return new ItemDto.ItemCommentDto(
//                comment.getId(),
//                comment.getText(),
//                comment.getAuthor().getName(),
//                comment.getCreated()
//        );
//    }
//
//    private Comment mapToComment(ItemDto.ItemCommentDto commentDto) {
//        Comment comment = new Comment();
//        comment.setId(commentDto.getId());
//        comment.setText(commentDto.getText());
//        comment.setCreated(commentDto.getCreated());
//        return comment;
//    }

