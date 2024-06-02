package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBookingOutDtoResponse(@RequestBody @Valid BookingRequest bookingRequest,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("пришел POST запрос /bookings с userId: {} и bookingDto: {}", userId, bookingRequest);
        BookingResponse bookingResponse = bookingService.create(bookingRequest, userId);
        log.info("отправлен ответ на POST запрос /bookings с userId: {} и bookingDto: {} с телом: {}", userId, bookingRequest, bookingResponse);
        return bookingResponse;
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingResponse setBookingOutDtoApprovalResponse(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam @NotNull Boolean approved,
                                         @PathVariable Long bookingId) {
        log.info("пришел PATCH запрос /booking/{bookingId} с userId: {} и approved: {} и bookingId: {}", userId, approved, bookingId);
        BookingResponse bookingResponse = bookingService.setBookingApproval(userId, approved, bookingId);
        log.info("отправлен ответ на PATCH запрос /booking/{bookingId} с userId: {} и approved: {} и bookingId: {} с телом: {}", userId, approved, bookingResponse);
        return bookingResponse;
    }

    @GetMapping("/{bookingId}")
    public BookingResponse findBookingOutDtoByIdResponse(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("пришел GET запрос /booking/{bookingId} с userId: {} и bookingId: {}", userId, bookingId);
        BookingResponse bookingResponse = bookingService.findBookingById(bookingId, userId);
        log.info("отправлен ответ на GET запрос /booking/{bookingId} с userId: {} и bookingId: {} с телом: {}", userId, bookingId, bookingResponse);
        return bookingResponse;
    }

    @GetMapping
    public List<BookingResponse> findBookingsOutDtoOfUserResponse(@RequestParam(defaultValue = "ALL", required = false) String state,
                                               @RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        BookingState bookingState = BookingState.valueOf(state);
        log.info("пришел GET запрос /bookings?state с userId: {}, state: {}, from: {}, size: {}", userId, state, from, size);
        List<BookingResponse> bookingResponse = bookingService.findBookingsOfUser(bookingState, userId, from, size);
        log.info("отправлен ответ на GET запрос /bookings?state с userId: {}, state: {}, from: {}, size: {}  с телом: {}", userId, state, from, size, bookingResponse);
        return bookingResponse;
    }

    @GetMapping("/owner")
    public List<BookingResponse> findBookingsOutDtoOfOwnerResponse(@RequestParam(defaultValue = "ALL", required = false) String state,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId, @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("пришел GET запрос /bookings/owner?state с userId: {} и state: {}", userId, state);
        BookingState bookingState = BookingState.valueOf(state);
        List<BookingResponse> bookingResponse = bookingService.findBookingsOfOwner(bookingState, userId, from, size);
        log.info("отправлен ответ на GET запрос /bookings/owner?state с userId: {} и state: {} с телом: {}", userId, state, bookingResponse);
        return bookingResponse;
    }
}
