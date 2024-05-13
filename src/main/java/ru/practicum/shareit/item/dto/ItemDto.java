package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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


    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}