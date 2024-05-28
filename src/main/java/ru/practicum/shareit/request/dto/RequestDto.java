package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private Integer id;

    @NotBlank
    @NotNull
    private String description;

    private LocalDateTime created;

    private List<RequestItemDto> items;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class RequestItemDto {

        private Integer id;

        @NotBlank
        private String name;

        @NotBlank
        private String description;

        @NotNull
        private Boolean available;

        private Integer requestId;

    }

}