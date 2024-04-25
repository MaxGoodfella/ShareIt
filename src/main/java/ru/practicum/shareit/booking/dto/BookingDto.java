package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BOOKING_STATUS;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    @NotNull
    private LocalDate start;

    @NotNull
    private LocalDate end;

    private Item item;

    private User booker;

    @NotNull
    private BOOKING_STATUS status;

}