package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.dto.ErrorResponse;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void handleUserValidationExceptionTest() {
        ErrorResponse response = errorHandler.handleUserValidationException(new UserValidationException(""));

        assertEquals(response, new ErrorResponse("Validation for user failed", ""));
    }

    @Test
    public void handleUserNotFoundExceptionTest() {
        ErrorResponse response = errorHandler.handleObjectNotFoundException(new BusinessObjectNotFoundException(""));

        assertEquals(response, new ErrorResponse("Search for object failed", ""));
    }

    @Test
    public void handleNotOwnerForbiddenExceptionTest() {
        ErrorResponse response = errorHandler.handleNotOwnerForbiddenException(new NotOwnerForbiddenException(""));

        assertEquals(response, new ErrorResponse("User must be the owner", ""));
    }

    @Test
    public void handleEmailConflictExceptionTest() {
        ErrorResponse response = errorHandler.handleEmailConflictException(new EmailConflictException(""));

        assertEquals(response, new ErrorResponse("Email conflict has occurred", ""));
    }

    @Test
    public void handleBookingValidationExceptionTest() {
        ErrorResponse response = errorHandler.handleBookingValidationException(new BookingValidationException(""));

        assertEquals(response, new ErrorResponse("Validation for booking failed", ""));
    }

    @Test
    public void handleUnsupportedBookingStateExceptionTest() {
        ErrorResponse response = errorHandler.handleUnsupportedBookingStateException(new UnsupportedBookingStateException(""));

        assertEquals(response, new ErrorResponse("error", ""));
    }

    @Test
    public void handleUserAccessForbiddenExceptionTest() {
        ErrorResponse response = errorHandler.handleUserAccessForbiddenException(new UserAccessForbiddenException(""));

        assertEquals(response, new ErrorResponse("User access denied", ""));
    }

    @Test
    public void handleCommentValidationExceptionTest() {
        ErrorResponse response = errorHandler.handleCommentValidationException(new CommentValidationException(""));

        assertEquals(response, new ErrorResponse("Validation for comment failed", ""));
    }

    @Test
    public void handleItemRequestNotFoundExceptionTest() {
        ErrorResponse response = errorHandler.handleItemRequestNotFoundException(new ItemRequestNotFoundException(""));

        assertEquals(response, new ErrorResponse("Search for ItemRequest failed", ""));
    }

    @Test
    public void handleUnknownExceptionTest() {
        ErrorResponse response = errorHandler.handleUnknownException(new Throwable(""));

        assertEquals(response, new ErrorResponse("Unknown error has occurred", ""));
    }
}
