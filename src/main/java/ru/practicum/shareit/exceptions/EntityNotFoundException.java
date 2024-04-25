package ru.practicum.shareit.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> entityClass, String message) {
        super("Entity '" + entityClass.getSimpleName() + "' not found. " + message);
    }

}