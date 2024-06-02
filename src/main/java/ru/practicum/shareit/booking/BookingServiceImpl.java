package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.BusinessObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponse create(BookingRequest bookingDto, Long userId) {
        User booker = UserMapper.toUser(userService.findUserById(userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new BusinessObjectNotFoundException("Item was not found"));
        if (!item.getAvailable()) {
            throw new BookingValidationException("Unable to create booking with an unavailable item");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new UserAccessForbiddenException("Owner of an item cannot rent it");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker, BookingStatus.WAITING);
        checkCorrectTiming(booking);
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse setBookingApproval(Long userId, Boolean approved, Long bookingId) {
        UserDto userDto = userService.findUserById(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BusinessObjectNotFoundException("Booking was not found"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            if (userId.equals(booking.getBooker().getId())) {
                throw new UserAccessForbiddenException("Booker cannot set approval");
            }
            throw new BookingValidationException("Only the owner of an item is allowed to set the booking approval");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingValidationException("Unable to set the approval to booking without status WAITING");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingResponse findBookingById(Long bookingId, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BusinessObjectNotFoundException("Booking was not found"));
        if (userId.equals(booking.getBooker().getId())
                || userId.equals(booking.getItem().getOwner().getId())) {
            return BookingMapper.toBookingDto(booking);
        }
        throw new UserAccessForbiddenException("Only the owner of an item or the booker are allowed to see " +
                "booking information");
    }

    @Override
    public List<BookingResponse> findBookingsOfUser(BookingState state, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case FUTURE:
                bookings = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                bookings = repository.findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(userId, now, now);
                break;
            default:
                throw new UnsupportedBookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    public List<BookingResponse> findBookingsOfOwner(BookingState state, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = repository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = repository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case FUTURE:
                bookings = repository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                bookings = repository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            default:
                throw new UnsupportedBookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    private void checkCorrectTiming(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        if (booking.getEnd().isBefore(now)) {
            throw new BookingValidationException("Unable to create booking with end time in the past");
        } else if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BookingValidationException("Unable to create booking with end time before start time");
        } else if (booking.getStart().isEqual(booking.getEnd())) {
            throw new BookingValidationException("Unable to create booking with end time equal to start time");
        } else if (booking.getStart().isBefore(now)) {
            throw new BookingValidationException("Unable to create booking with start time in the past");
        }
    }
}
