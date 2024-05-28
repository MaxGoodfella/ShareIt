package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoOutTest {

    @Autowired
    private JacksonTester<CommentDtoOut> json;


    @Test
    void testCommentDto() throws Exception {

        LocalDateTime createdDateTime = LocalDateTime.now();

        ItemDtoOut itemDtoOut = ItemDtoOut.builder()
                .id(1)
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        CommentDtoOut commentDtoOut = CommentDtoOut.builder()
                .id(1)
                .text("text")
                .authorName("name")
                .created(createdDateTime)
                .itemId(itemDtoOut.getId())
                .build();

        JsonContent<CommentDtoOut> result = json.write(commentDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(createdDateTime.toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);

    }

}