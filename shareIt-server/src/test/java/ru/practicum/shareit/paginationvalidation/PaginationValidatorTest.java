package ru.practicum.shareit.paginationvalidation;
import ru.practicum.shareit.exceptions.BadRequestException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaginationValidatorTest {

    private final PaginationValidator paginationValidator = new PaginationValidator();


    @Test
    void validateSearchParameters_InvalidParameters_FromAndSizeZero() {
        assertThrows(BadRequestException.class, () -> paginationValidator.validateSearchParameters(0, 0));
    }

    @Test
    void validateSearchParameters_InvalidParameters_FromNegative() {
        assertThrows(BadRequestException.class, () -> paginationValidator.validateSearchParameters(-1, 10));
    }

    @Test
    void validateSearchParameters_InvalidParameters_SizeNegative() {
        assertThrows(BadRequestException.class, () -> paginationValidator.validateSearchParameters(0, -1));
    }

    @Test
    void validateSearchParameters_ValidParameters() {
        paginationValidator.validateSearchParameters(1, 10); // No exception expected
    }

}