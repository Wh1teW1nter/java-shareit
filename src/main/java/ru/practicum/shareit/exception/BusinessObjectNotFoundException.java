package ru.practicum.shareit.exception;

public class BusinessObjectNotFoundException   extends RuntimeException {

    public BusinessObjectNotFoundException(String message) {
        super(message);
    }
}
