package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingTimeState;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Integer id;

    // @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    // @NotNull
    @Future
    private LocalDateTime end;

    @NotNull
    private Integer itemId;

    private BookingState state;

    private BookingTimeState bookingTimeState;


    @AssertTrue
    boolean isEndAfterStart() {

        if (end == null || start == null) {
            return false;
        }

        return end.isAfter(start);
    }

}