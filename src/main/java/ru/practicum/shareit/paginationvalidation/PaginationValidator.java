package ru.practicum.shareit.paginationvalidation;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.BadRequestException;


@RestControllerAdvice
public class PaginationValidator {

    public void validateSearchParameters(Integer from, Integer size) {
        if (from == 0 && size == 0) {
            throw new BadRequestException(Integer.class, from + " & " + size,
                    "Некорректные параметры поиска: from = " + from + " и " + " size = " + size);
        }

        if (from < 0 || size <= 0) {
            throw new BadRequestException(Integer.class, from + " & " + size,
                    "Некорректные параметры поиска: from = " + from + " и " + " size = " + size);
        }
    }

}