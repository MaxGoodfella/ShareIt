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


    @Test
    void testRequestDto() throws Exception {

        LocalDateTime createdDateTime = LocalDateTime.now();

        RequestDto requestDto = RequestDto.builder()
                .id(1)
                .description("description")
                .created(createdDateTime)
                .items(Collections.emptyList())
                .build();

        JsonContent<RequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(createdDateTime.toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();

    }

}