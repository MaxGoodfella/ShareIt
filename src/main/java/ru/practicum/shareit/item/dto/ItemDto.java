package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private ItemBookingDto lastBooking;

    private ItemBookingDto nextBooking;

    private List<ItemCommentDto> comments;

    private Integer requestId;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class ItemBookingDto {

        Integer id;

        LocalDateTime start;

        LocalDateTime end;

        Integer bookerId;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ItemCommentDto {

        Integer id;

        @NotNull
        private String text;

        private String authorName;

        LocalDateTime created;

    }

}