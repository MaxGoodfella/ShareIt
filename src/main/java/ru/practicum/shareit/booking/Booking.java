package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class Booking {

    private Integer id;

    @NotNull
    private LocalDate start;

    @NotNull
    private LocalDate end;

    private Item item;

    private User booker;

    @NotNull
    private BookingStatus status;

}