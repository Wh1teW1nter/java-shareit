package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse create(BookingRequest bookingDto, Long userId);

    BookingResponse setBookingApproval(Long userId, Boolean approved, Long bookingId);

    BookingResponse findBookingById(Long bookingId, Long userId);

    List<BookingResponse> findBookingsOfUser(BookingState state, Long userId);

    List<BookingResponse> findBookingsOfOwner(BookingState state, Long userId);
}
