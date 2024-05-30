package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {

        LocalDateTime now = LocalDateTime.now().withNano(0);

        ItemDto.ItemBookingDto itemBookingDto1 = ItemDto.ItemBookingDto.builder()
                .id(1)
                .start(now.minusDays(2).withNano(0))
                .end(now.minusDays(1).withNano(0))
                .bookerId(1)
                .build();

        ItemDto.ItemBookingDto itemBookingDto2 = ItemDto.ItemBookingDto.builder()
                .id(1)
                .start(now.plusDays(1).withNano(0))
                .end(now.plusDays(2).withNano(0))
                .bookerId(1)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(itemBookingDto1)
                .nextBooking(itemBookingDto2)
                .comments(Collections.emptyList())
                .requestId(1)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(now.minusDays(2).withNano(0).toString());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(now.minusDays(1).withNano(0).toString());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(now.plusDays(1).withNano(0).toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(now.plusDays(2).withNano(0).toString());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);

        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();

        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

}