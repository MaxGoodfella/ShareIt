package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ItemRequest {

    private Integer id;

    @NotBlank
    private String description;

    private User requestor;

    @NotNull
    private LocalDate created;

}