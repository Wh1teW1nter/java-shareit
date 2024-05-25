package ru.practicum.shareit.exception;

public class NotOwnerForbiddenException extends RuntimeException {
    public NotOwnerForbiddenException(String message) {
        super(message);
    }
}
