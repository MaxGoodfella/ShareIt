package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoNestedClassesTest {

    @Autowired
    private JacksonTester<ItemDto.ItemBookingDto> jsonBooking;

    @Autowired
    private JacksonTester<ItemDto.ItemCommentDto> jsonComment;


    @Test
    void testItemBookingDto() throws Exception {

        LocalDateTime now = LocalDateTime.now().withNano(0);

        ItemDto.ItemBookingDto bookingDto = ItemDto.ItemBookingDto.builder()
                .id(1)
                .start(now.minusDays(1).withNano(0))
                .end(now.plusDays(1).withNano(0))
                .bookerId(123)
                .build();

        JsonContent<ItemDto.ItemBookingDto> result = jsonBooking.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(now.minusDays(1).withNano(0).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(now.plusDays(1).withNano(0).toString());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(123);

    }

    @Test
    void testItemCommentDto() throws Exception {

        LocalDateTime now = LocalDateTime.now().withNano(0);

        ItemDto.ItemCommentDto commentDto = ItemDto.ItemCommentDto.builder()
                .id(1)
                .text("text")
                .authorName("name")
                .created(now.withNano(0))
                .build();

        JsonContent<ItemDto.ItemCommentDto> result = jsonComment.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(now.withNano(0).toString());

    }

}