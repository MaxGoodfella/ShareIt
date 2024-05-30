package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoOutTest {

    @Autowired
    private JacksonTester<BookingDtoOut> json;


    @Test
    void testBookingDtoOut() throws Exception {
        LocalDateTime createdDateTime = LocalDateTime.now();

        ItemDtoOut itemDtoOut = ItemDtoOut.builder()
                .id(1)
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .id(1)
                .name("username")
                .email("user@email.com")
                .build();

        BookingDtoOut bookingDtoOut = BookingDtoOut.builder()
                .id(1)
                .item(itemDtoOut)
                .start(createdDateTime.plusDays(1))
                .end(createdDateTime.plusDays(2))
                .booker(userDto)
                .status(BookingState.WAITING)
                .build();

        JsonContent<BookingDtoOut> result = json.write(bookingDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(createdDateTime.plusDays(1)
                .toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(createdDateTime.plusDays(2)
                .toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

}