package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private final LocalDateTime created = LocalDateTime.now();

    private final RequestDto requestDto = RequestDto.builder()
            .id(1)
            .description("description")
            .created(created)
            .items(Collections.emptyList())
            .build();


    @SneakyThrows
    @Test
    void add() {
        User user = new User(1, "username", "user@email.com");
        Request request = new Request(1, "description", user, created);

        when(requestService.add(anyInt(), any())).thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId())))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(user.getId())))
                .andExpect(jsonPath("$.requestor.name", is(user.getName())))
                .andExpect(jsonPath("$.requestor.email", is(user.getEmail())))
                .andExpect(jsonPath("$.created", startsWith(requestDto.getCreated().toString()
                        .substring(0, 19))));
    }

    @SneakyThrows
    @Test
    void getRequestsSent() {
        when(requestService.getRequestsSent(anyInt())).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto.getId())))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", startsWith(requestDto.getCreated().toString()
                        .substring(0, 19))))
                .andExpect(jsonPath("$[0].items", is(requestDto.getItems())));
    }

    @SneakyThrows
    @Test
    void getRequest() {
        when(requestService.getRequest(anyInt(), anyInt())).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId())))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", startsWith(requestDto.getCreated().toString()
                        .substring(0, 19))))
                .andExpect(jsonPath("$.items", is(requestDto.getItems())));
    }

    @SneakyThrows
    @Test
    void getRequests() {
        when(requestService.getRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto.getId())))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", startsWith(requestDto.getCreated().toString()
                        .substring(0, 19))))
                .andExpect(jsonPath("$[0].items", is(requestDto.getItems())));
    }


}