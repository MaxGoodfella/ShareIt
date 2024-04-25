package ru.practicum.shareit.exceptions;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(Class<?> entityClass, String message) {
        super("No access allowed. " + message);
    }

}