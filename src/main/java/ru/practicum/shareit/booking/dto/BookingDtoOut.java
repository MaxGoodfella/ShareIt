package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoOut {
    private Integer id;
    private ItemDtoOut item;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private BookingState status;

    public Integer getItemId() {
        return item.getId();
    }

    public int getBookerId() {
        return booker.getId();
    }
}