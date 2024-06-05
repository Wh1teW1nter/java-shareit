package ru.practicum.shareit.exception;

public class UnsupportedBookingStateException extends RuntimeException {

    public UnsupportedBookingStateException(String message) {
        super(message);
    }
}
