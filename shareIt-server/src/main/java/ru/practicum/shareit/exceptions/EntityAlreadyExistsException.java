package ru.practicum.shareit.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(Class<?> entityClass, String message) {
        super(String.format("Entity '%s' already exists. %s",
                entityClass.getSimpleName(), message));
    }

}