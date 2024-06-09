package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    @NotBlank
    private String description;

}