package ru.practicum.shareit.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> entityClass, String entityId, String message) {
        super(String.format("Entity '%s' with ID '%s' not found. %s",
                entityClass.getSimpleName(), entityId, message));
    }

}