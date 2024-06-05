package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void handleUserValidationExceptionTest() {
        Map<String, String> response = errorHandler.handleUserValidationException(new UserValidationException(""));

        assertEquals(response, Map.of("Validation for user failed", ""));
    }

    @Test
    public void handleUserNotFoundExceptionTest() {
        Map<String, String> response = errorHandler.handleUserNotFoundException(new UserNotFoundException(""));

        assertEquals(response, Map.of("Search for user failed", ""));
    }

    @Test
    public void handleItemNotFoundExceptionTest() {
        Map<String, String> response = errorHandler.handleItemNotFoundException(new ItemNotFoundException(""));

        assertEquals(response, Map.of("Search for item failed", ""));
    }

    @Test
    public void handleNotOwnerForbiddenExceptionTest() {
        Map<String, String> response = errorHandler.handleNotOwnerForbiddenException(new NotOwnerForbiddenException(""));

        assertEquals(response, Map.of("User must be the owner", ""));
    }

    @Test
    public void handleEmailConflictExceptionTest() {
        Map<String, String> response = errorHandler.handleEmailConflictException(new EmailConflictException(""));

        assertEquals(response, Map.of("Email conflict has occurred", ""));
    }

    @Test
    public void handleBookingNotFoundExceptionTest() {
        Map<String, String> response = errorHandler.handleBookingNotFoundException(new BookingNotFoundException(""));

        assertEquals(response, Map.of("Search for booking failed", ""));
    }

    @Test
    public void handleBookingValidationExceptionTest() {
        Map<String, String> response = errorHandler.handleBookingValidationException(new BookingValidationException(""));

        assertEquals(response, Map.of("Validation for booking failed", ""));
    }

    @Test
    public void handleUnsupportedBookingStateExceptionTest() {
        Map<String, String> response = errorHandler.handleUnsupportedBookingStateException(new UnsupportedBookingStateException(""));

        assertEquals(response, Map.of("error", ""));
    }

    @Test
    public void handleUserAccessForbiddenExceptionTest() {
        Map<String, String> response = errorHandler.handleUserAccessForbiddenException(new UserAccessForbiddenException(""));

        assertEquals(response, Map.of("User access denied", ""));
    }

    @Test
    public void handleCommentValidationExceptionTest() {
        Map<String, String> response = errorHandler.handleCommentValidationException(new CommentValidationException(""));

        assertEquals(response, Map.of("Validation for comment failed", ""));
    }

    @Test
    public void handleItemRequestNotFoundExceptionTest() {
        Map<String, String> response = errorHandler.handleItemRequestNotFoundException(new ItemRequestNotFoundException(""));

        assertEquals(response, Map.of("Search for ItemRequest failed", ""));
    }

    @Test
    public void handleUnknownExceptionTest() {
        Map<String, String> response = errorHandler.handleUnknownException(new Throwable(""));

        assertEquals(response, Map.of("Unknown error has occurred", ""));
    }
}
