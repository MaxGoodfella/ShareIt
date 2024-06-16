package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDtoOut toItemDtoOut(Item item) {

        return new ItemDtoOut(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public ItemDtoOut toItemDtoOut(Item item, BookingDtoOut lastBooking,
                                   List<CommentDtoOut> comments, BookingDtoOut nextBooking) {
        return new ItemDtoOut(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                comments,
                nextBooking,
                item.getRequestId());
    }

    public RequestDto.RequestItemDto toRequestItemDto(Item item) {
        return new RequestDto.RequestItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

}