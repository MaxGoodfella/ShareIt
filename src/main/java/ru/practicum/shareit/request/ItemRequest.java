package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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