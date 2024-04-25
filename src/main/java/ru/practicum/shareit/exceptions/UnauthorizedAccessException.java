package ru.practicum.shareit.exceptions;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(Class<?> entityClass, String message) {
        super("No access allowed. " + message);
    }

}
