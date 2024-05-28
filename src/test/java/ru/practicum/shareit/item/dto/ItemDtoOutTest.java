package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoOutTest {

    @Autowired
    private JacksonTester<ItemDtoOut> json;


    @Test
    void testItemDtoOut() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        UserDto userDto = UserDto.builder()
                .id(1)
                .name("username")
                .email("user@email.com")
                .build();

        ItemDtoOut item = ItemDtoOut.builder()
                .id(1)
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        BookingDtoOut bookingDtoOut1 = BookingDtoOut.builder()
                .id(1)
                .item(item)
                .start(LocalDateTime.parse(now.minusDays(2).format(formatter)))
                .end(LocalDateTime.parse(now.minusDays(1).format(formatter)))
                .booker(userDto)
                .status(BookingState.WAITING)
                .build();

        BookingDtoOut bookingDtoOut2 = BookingDtoOut.builder()
                .id(2)
                .item(item)
                .start(LocalDateTime.parse(now.plusDays(1).format(formatter)))
                .end(LocalDateTime.parse(now.plusDays(2).format(formatter)))
                .booker(userDto)
                .status(BookingState.WAITING)
                .build();

        ItemDtoOut itemDtoOut = ItemDtoOut.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(bookingDtoOut1)
                .comments(Collections.emptyList())
                .nextBooking(bookingDtoOut2)
                .requestId(1)
                .build();

        JsonContent<ItemDtoOut> result = json.write(itemDtoOut);


        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");

        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(now.minusDays(2).format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(now.minusDays(1).format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo("WAITING");

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(now.plusDays(1).format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(now.plusDays(2).format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo("WAITING");

        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();

        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);

    }

}