package ru.practicum.shareit.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(Class<?> entityClass, String message) {
        super("Entity '" + entityClass.getSimpleName() + "' already exists. " + message);
    }

}