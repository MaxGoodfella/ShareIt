package ru.practicum.shareit.exceptions;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(Class<?> entityClass, String entityId, String message) {
        super(String.format("No access allowed to entity '%s' for user with ID '%s'. %s",
                entityClass.getSimpleName(), entityId, message));
    }

}