package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class RequestDtoTest {

    @Autowired
    JacksonTester<RequestDto> json;

    @Autowired
    private JacksonTester<RequestDto.RequestItemDto> jsonItemDto;


    @Test
    void testRequestDto() throws Exception {

        LocalDateTime createdDateTime = LocalDateTime.now().withNano(4);

        RequestDto requestDto = RequestDto.builder()
                .id(1)
                .description("description")
                .created(createdDateTime.withNano(4))
                .items(Collections.emptyList())
                .build();

        JsonContent<RequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(createdDateTime
                .withNano(4).toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();

    }

    @Test
    void testRequestItemDto() throws Exception {

        RequestDto.RequestItemDto itemDto = RequestDto.RequestItemDto.builder()
                .id(1)
                .name("Test Name")
                .description("Test Description")
                .available(true)
                .requestId(123)
                .build();

        assertThat(jsonItemDto.write(itemDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonItemDto.write(itemDto)).extractingJsonPathStringValue("$.name")
                .isEqualTo("Test Name");
        assertThat(jsonItemDto.write(itemDto)).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test Description");
        assertThat(jsonItemDto.write(itemDto)).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(true);
        assertThat(jsonItemDto.write(itemDto)).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(123);

    }

}