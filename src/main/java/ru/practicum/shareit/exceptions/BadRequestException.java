package ru.practicum.shareit.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(Class<?> entityClass, String message) {
        super("Entity '" + entityClass.getSimpleName() + "' cannot be requested. " + message);
    }

}
