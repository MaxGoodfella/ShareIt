package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class CustomExceptionHandlerTest {

    private final CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler();


    @Test
    void handleAccessDeniedException_ShouldReturnForbiddenErrorResponse() {
        AccessDeniedException exception = mock(AccessDeniedException.class);
        ErrorResponse response = customExceptionHandler.handleAccessDeniedException(exception);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatus());
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundErrorResponse() {
        EntityNotFoundException exception = mock(EntityNotFoundException.class);
        ErrorResponse response = customExceptionHandler.handleEntityNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void handleEntityAlreadyExistsException_ShouldReturnConflictErrorResponse() {
        EntityAlreadyExistsException exception = mock(EntityAlreadyExistsException.class);
        ErrorResponse response = customExceptionHandler.handleEntityAlreadyExistsException(exception);
        assertEquals(HttpStatus.CONFLICT, response.getStatus());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerErrorErrorResponse() {
        Throwable throwable = mock(Throwable.class);
        ErrorResponse response = customExceptionHandler.handleThrowable(throwable);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
    }
}