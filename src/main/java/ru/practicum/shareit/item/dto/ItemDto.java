package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Getter
@Setter
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


    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ItemBookingDto {

        Integer id;

        LocalDateTime start;

        LocalDateTime end;

        Integer bookerId;

    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemCommentDto {

        Integer id;

        @NotNull
        private String text;

        private String authorName;

        LocalDateTime created;

    }

}