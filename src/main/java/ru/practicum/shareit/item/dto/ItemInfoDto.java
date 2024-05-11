package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoDto {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

//    private BookingDto lastBooking;
//
//    private BookingDto nextBooking;


//    @Getter
//    @Setter
//    @AllArgsConstructor
//    private static class BookingDto {
//
//        Integer id;
//
//        private LocalDateTime start;
//
//        private LocalDateTime end;
//
//        private Integer itemId;
//
//        private BookingState state;
//
//    }

}
