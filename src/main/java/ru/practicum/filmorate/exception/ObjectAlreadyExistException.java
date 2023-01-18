package ru.practicum.filmorate.exception;

public class ObjectAlreadyExistException extends RuntimeException {

    public ObjectAlreadyExistException(String message) {
        super(message);
    }
}
