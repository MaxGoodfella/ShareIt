package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final User user = new User(1, "username", "user@email.com");

    private final Item item = new Item(1, "item name", "item description",
            true, user, null);

    private final ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("item name")
            .description("item description")
            .available(true)
            .build();

    private final ItemDtoOut itemDtoOut = ItemDtoOut.builder()
            .id(1)
            .name("item name")
            .description("item description")
            .available(true)
            .build();


    @SneakyThrows
    @Test
    void add() {
        when(itemService.add(anyInt(), any(ItemDto.class))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @SneakyThrows
    @Test
    void update() {
        when(itemService.update(anyInt(), anyInt(), any(ItemDto.class))).thenReturn(item);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getItemByItemId() {
        when(itemService.getItem(anyInt(), anyInt())).thenReturn(itemDtoOut);

        mockMvc.perform(get("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getItemsByUserId() {
        List<ItemDtoOut> items = Collections.singletonList(itemDtoOut);
        when(itemService.getItems(anyInt(), anyInt(), anyInt())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("from", "0")
                        .param("size", "100")
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable())));
    }

    @SneakyThrows
    @Test
    void search() {
        List<Item> items = Collections.singletonList(item);
        when(itemService.search(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "item name")
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));
    }

    @SneakyThrows
    @Test
    void addComment() {
        ItemDto.ItemCommentDto comment = ItemDto.ItemCommentDto.builder()
                .id(1)
                .text("comment")
                .authorName("username")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(anyInt(), anyInt(), any(ItemDto.ItemCommentDto.class))).thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created").exists());
    }

}