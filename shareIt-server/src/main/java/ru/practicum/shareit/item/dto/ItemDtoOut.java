package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDtoOut {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoOut lastBooking;

    private List<CommentDtoOut> comments;

    private BookingDtoOut nextBooking;

    private Integer requestId;


    public ItemDtoOut(Integer id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

}