package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingTimeState;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    JacksonTester<BookingDto> json;


    @Test
    void testBookingDto() throws Exception {

        LocalDateTime createdDateTime = LocalDateTime.now().withNano(0);

        ItemDtoOut itemDtoOut = ItemDtoOut.builder()
                .id(1)
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .start(createdDateTime.plusDays(1).withNano(0))
                .end(createdDateTime.plusDays(2).withNano(0))
                .itemId(itemDtoOut.getId())
                .status(BookingState.WAITING)
                .bookingTimeState(BookingTimeState.ALL)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(createdDateTime.plusDays(1)
                .withNano(0).toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(createdDateTime.plusDays(2)
                .withNano(0).toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.bookingTimeState").isEqualTo("ALL");

    }

}