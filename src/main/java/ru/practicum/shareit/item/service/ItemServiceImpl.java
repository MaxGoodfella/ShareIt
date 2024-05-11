package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.JpaCommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.*;

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


    @Override
    public ItemDto getItem(Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class,
                        "Вещь с id " + itemId + " не найдена."));

        ItemDto itemDto = modelMapper.map(item, ItemDto.class);

        List<ItemDto.ItemCommentDto> commentDtos = getCommentDtosForItem(itemId);
        itemDto.setComments(commentDtos);

        setLastAndNextBooking(itemDto, item.getId());

        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class,
                        "Пользователь с id " + userId + " не найден."));

        List<Item> items = itemRepository.findByOwnerId(userId);
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            ItemDto itemDto = modelMapper.map(item, ItemDto.class);

            List<ItemDto.ItemCommentDto> commentDtos = getCommentDtosForItem(item.getId());
            itemDto.setComments(commentDtos);

            setLastAndNextBooking(itemDto, item.getId());

            itemDtos.add(itemDto);
        }

        return itemDtos;
    }





//    @Override
//    public Item getItem(Integer itemId) {
//        Optional<Item> item = itemRepository.findById(itemId);
//        return item.orElseThrow(() -> new EntityNotFoundException(Item.class,
//                "Вещь с id " + itemId + " не найдена."));
//    }

//    @Override
//    public ItemDto getItem(Integer userId, Integer itemId) {
////        Optional<Item> item = itemRepository.findById(itemId);
////        return item.orElseThrow(() -> new EntityNotFoundException(Item.class,
////                "Вещь с id " + itemId + " не найдена."));
//
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new EntityNotFoundException(Item.class,
//                        "Вещь с id " + itemId + " не найдена."));
//
//
//        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
//
//        List<Comment> comments = commentRepository.findAllByItemId(itemId);
//        List<ItemDto.ItemCommentDto> commentDtos = new ArrayList<>();
//        for (Comment comment : comments) {
//            commentDtos.add(mapToItemCommentDto(comment));
//        }
//        itemDto.setComments(commentDtos);
//
//        if (item.getOwner().getId().equals(userId)) {
//
//        }
//
//    }


    // рабочий ниже

//    @Override
//    public ItemDto getItem(Integer userId, Integer itemId) {
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new EntityNotFoundException(Item.class,
//                        "Вещь с id " + itemId + " не найдена."));
//
//        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
//
//        // Получаем комментарии для предмета
//        List<Comment> comments = commentRepository.findAllByItemId(itemId);
//        List<ItemDto.ItemCommentDto> commentDtos = new ArrayList<>();
//        for (Comment comment : comments) {
//            commentDtos.add(mapToItemCommentDto(comment));
//        }
//        itemDto.setComments(commentDtos);
//
//        // Устанавливаем lastBooking и nextBooking
//        List<Booking> bookings = bookingRepository.findBookingsByItem_Owner_Id(item.getId());
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
//            pastBookings.sort(Comparator.comparing(Booking::getEnd).reversed());
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
//
//        return itemDto;
//    }



//    @Override
//    public List<Item> getItems(Integer userId) {
//        userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException(User.class,
//                        "Пользователь с id " + userId + " не найден."));
//
//        return itemRepository.findByOwnerId(userId);
//    }



    // рабочий ниже

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
//            // Получаем комментарии для каждого предмета
//            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
//            List<ItemDto.ItemCommentDto> commentDtos = new ArrayList<>();
//            for (Comment comment : comments) {
//                commentDtos.add(mapToItemCommentDto(comment));
//            }
//            itemDto.setComments(commentDtos);
//
//            // Добавляем информацию о ближайшем прошлом и будущем бронированиях
//
//            List<Booking> bookings = bookingRepository.findBookingsByItem_Owner_Id(item.getId());
//            if (!bookings.isEmpty()) {
//                List<Booking> pastBookings = new ArrayList<>();
//                List<Booking> futureBookings = new ArrayList<>();
//                LocalDateTime now = LocalDateTime.now();
//
//                for (Booking booking : bookings) {
//                    if (booking.getEnd().isBefore(now)) {
//                        pastBookings.add(booking);
//                    } else {
//                        futureBookings.add(booking);
//                    }
//                }
//
//                pastBookings.sort(Comparator.comparing(Booking::getEnd).reversed());
//                futureBookings.sort(Comparator.comparing(Booking::getStart));
//
//                Booking closestPastBooking = pastBookings.isEmpty() ? null : pastBookings.get(0);
//                Booking closestFutureBooking = futureBookings.isEmpty() ? null : futureBookings.get(0);
//
//                if (closestPastBooking != null) {
//                    itemDto.setLastBooking(mapToItemBookingDto(closestPastBooking));
//                }
//                if (closestFutureBooking != null) {
//                    itemDto.setNextBooking(mapToItemBookingDto(closestFutureBooking));
//                }
//            }
//
//            itemDtos.add(itemDto);
//        }
//
//        return itemDtos;
//    }




//    @Override
//    public List<ItemDto> getItems(Integer userId) { // вроде работает
//        userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException(User.class,
//                        "Пользователь с id " + userId + " не найден."));
//
//        List<Item> items = itemRepository.findByOwnerId(userId);
//        List<ItemDto> itemDtos = new ArrayList<>();
//
//        for (Item item : items) {
//            ItemDto itemDto = modelMapper.map(item, ItemDto.class);
//
//            List<Booking> bookings = bookingRepository.findBookingsByItem_Owner_Id(item.getId());
//            if (!bookings.isEmpty()) {
//
//                List<Booking> pastBookings = new ArrayList<>();
//                List<Booking> futureBookings = new ArrayList<>();
//
//                LocalDateTime now = LocalDateTime.now();
//
//                for (Booking booking : bookings) {
//                    if (booking.getEnd().isBefore(now)) {
//                        pastBookings.add(booking);
//                    } else {
//                        futureBookings.add(booking);
//                    }
//                }
//
//                pastBookings.sort(Comparator.comparing(Booking::getEnd).reversed());
//                futureBookings.sort(Comparator.comparing(Booking::getStart));
//
//                Booking closestPastBooking = pastBookings.isEmpty() ? null : pastBookings.get(0);
//                Booking closestFutureBooking = futureBookings.isEmpty() ? null : futureBookings.get(0);
//
//                if (closestPastBooking != null) {
//                    itemDto.setLastBooking(mapToItemBookingDto(closestPastBooking));
//                }
//                if (closestFutureBooking != null) {
//                    itemDto.setNextBooking(mapToItemBookingDto(closestFutureBooking));
//                }
//
//            }
//
//            itemDtos.add(itemDto);
//        }
//
//        return itemDtos;
//    }

//    @Override
//    public List<ItemDto> getItems(Integer userId) {
//        userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException(User.class,
//                        "Пользователь с id " + userId + " не найден."));
//
//        List<Item> items = itemRepository.findByOwnerId(userId);
//        List<ItemDto> itemDtos = new ArrayList<>();
//
//        for (Item item : items) {
//            ItemDto itemDto = modelMapper.map(item, ItemDto.class);
//
//            List<Booking> bookings = bookingRepository.findByItemIdOrderByEndAsc(item.getId());
//            if (!bookings.isEmpty()) {
//
//                Booking lastBooking = bookings.get(bookings.size() - 1);
//                ItemBookingDto lastBookingDto = new ItemDto.ItemBookingDto(
//                        lastBooking.getId(),
//                        lastBooking.getStart(),
//                        lastBooking.getEnd(),
//                        lastBooking.getBooker().getId()
//                );
//                itemDto.setLastBooking(lastBookingDto);
//
//                Booking nextBooking = bookings.get(0);
//                ItemBookingDto nextBookingDto = new ItemBookingDto(
//                        nextBooking.getId(),
//                        nextBooking.getStart(),
//                        nextBooking.getEnd(),
//                        nextBooking.getBooker().getId()
//                );
//                itemDto.setNextBooking(nextBookingDto);
//            }
//
//            itemDtos.add(itemDto);
//        }
//
//        return itemDtos;
//    }

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


        List<Booking> userBookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);
        boolean hasApprovedBooking = false;

        commentDto.setCreated(LocalDateTime.now());

        for (Booking booking : userBookings) {
            if (booking.getState() == BookingState.APPROVED &&
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



    private List<ItemDto.ItemCommentDto> getCommentDtosForItem(Integer itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<ItemDto.ItemCommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(mapToItemCommentDto(comment));
        }
        return commentDtos;
    }

    private void setLastAndNextBooking(ItemDto itemDto, Integer itemId) {
        List<Booking> bookings = bookingRepository.findBookingsByItem_Owner_Id(itemId);
        if (!bookings.isEmpty()) {
            List<Booking> pastBookings = new ArrayList<>();
            List<Booking> futureBookings = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (Booking booking : bookings) {
                if (booking.getEnd().isBefore(now)) {
                    pastBookings.add(booking);
                } else {
                    futureBookings.add(booking);
                }
            }

            pastBookings.sort(Comparator.comparing(Booking::getEnd).reversed());
            futureBookings.sort(Comparator.comparing(Booking::getStart));

            Booking closestPastBooking = pastBookings.isEmpty() ? null : pastBookings.get(0);
            Booking closestFutureBooking = futureBookings.isEmpty() ? null : futureBookings.get(0);

            if (closestPastBooking != null) {
                itemDto.setLastBooking(mapToItemBookingDto(closestPastBooking));
            }
            if (closestFutureBooking != null) {
                itemDto.setNextBooking(mapToItemBookingDto(closestFutureBooking));
            }
        }
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