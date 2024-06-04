package ru.practicum.shareit.exception;

public class UserAccessForbiddenException extends RuntimeException {

    public UserAccessForbiddenException(String message) {
        super(message);
    }
}
