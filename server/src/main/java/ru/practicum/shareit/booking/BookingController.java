package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto create(@RequestBody BookingDto bookingDto,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingOutDto setBookingApproval(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam Boolean approved,
                                            @PathVariable Long bookingId) {
        return bookingService.setBookingApproval(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto findBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutDto> findBookingsOfUser(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        BookingState bookingState = BookingState.valueOf(state);
        return bookingService.findBookingsOfUser(bookingState, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> findBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        BookingState bookingState = BookingState.valueOf(state);
        return bookingService.findBookingsOfOwner(bookingState, userId, from, size);
    }
}
