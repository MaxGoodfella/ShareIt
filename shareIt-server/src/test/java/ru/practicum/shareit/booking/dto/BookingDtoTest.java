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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@JsonTest
class BookingDtoTest {

    @Autowired
    JacksonTester<BookingDto> json;


    @Test
    void testBookingDto() throws Exception {

        LocalDateTime createdDateTime = LocalDateTime.now().withNano(4);

        ItemDtoOut itemDtoOut = ItemDtoOut.builder()
                .id(1)
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1)
                .start(createdDateTime.plusDays(1).withNano(4))
                .end(createdDateTime.plusDays(2).withNano(4))
                .itemId(itemDtoOut.getId())
                .status(BookingState.WAITING)
                .bookingTimeState(BookingTimeState.ALL)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(createdDateTime.plusDays(1)
                .withNano(4).toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(createdDateTime.plusDays(2)
                .withNano(4).toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.bookingTimeState").isEqualTo("ALL");

    }

    @Test
    void isEndAfterStart_WhenEndIsNull_ReturnsFalse() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        assertFalse(bookingDto.isEndAfterStart());
    }

    @Test
    void isEndAfterStart_WhenStartIsNull_ReturnsFalse() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now());
        assertFalse(bookingDto.isEndAfterStart());
    }

    @Test
    void isEndAfterStart_WhenEndBeforeStart_ReturnsFalse() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusHours(1));
        assertFalse(bookingDto.isEndAfterStart());
    }

    @Test
    void isEndAfterStart_WhenEndEqualsStart_ReturnsFalse() {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(now);
        bookingDto.setEnd(now);
        assertFalse(bookingDto.isEndAfterStart());
    }

    @Test
    void isEndAfterStart_WhenEndAfterStart_ReturnsTrue() {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(now);
        bookingDto.setEnd(now.plusHours(1));
        assertTrue(bookingDto.isEndAfterStart());
    }

}