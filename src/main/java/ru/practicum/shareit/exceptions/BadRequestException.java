package ru.practicum.shareit.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(Class<?> entityClass, String entityId, String message) {
        super(String.format("Entity '%s' with ID '%s' cannot be requested. %s",
                entityClass.getSimpleName(), entityId, message));
    }

}