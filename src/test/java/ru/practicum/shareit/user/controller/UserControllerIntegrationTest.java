package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final User user = new User(1, "username", "user@email.com");

    User badUser = new User("username", null);


    @SneakyThrows
    @Test
    void save() {

        when(userService.save(any())).thenReturn(user);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName()), String.class))
                .andExpect(jsonPath("$.email", is(user.getEmail()), String.class));

    }

    @SneakyThrows
    @Test
    void update() {

        when(userService.update(anyInt(), any())).thenReturn(user);

        mockMvc.perform(patch("/users/1", user.getId())
                        .content(objectMapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findById() {

        when(userService.findById(anyInt())).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

    }

    @SneakyThrows
    @Test
    void findAll() {

        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));

    }


    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService, times(1))
                .deleteById(anyInt());
    }

}