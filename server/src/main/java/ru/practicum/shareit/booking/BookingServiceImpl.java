package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.*;
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
    public BookingOutDto create(BookingDto bookingDto, Long userId) {
        User booker = UserMapper.toUser(userService.findUserById(userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item was not found"));
        if (!item.getAvailable()) {
            throw new BookingValidationException("Unable to create booking with an unavailable item");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new UserAccessForbiddenException("Owner of an item cannot rent it");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        checkCorrectTiming(booking);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutDto setBookingApproval(Long userId, Boolean approved, Long bookingId) {
        UserDto userDto = userService.findUserById(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking was not found"));
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
    public BookingOutDto findBookingById(Long bookingId, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking was not found"));
        if (userId.equals(booking.getBooker().getId())
                || userId.equals(booking.getItem().getOwner().getId())) {
            return BookingMapper.toBookingDto(booking);
        }
        throw new UserAccessForbiddenException("Only the owner of an item or the booker are allowed to see " +
                "booking information");
    }

    @Override
    public List<BookingOutDto> findBookingsOfUser(BookingState state, Long userId, int from, int size) {
        UserDto userDto = userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        Sort sort = Sort.by("start").descending();
        Pageable pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        switch (state) {
            case ALL:
                bookings = repository.findAllByBookerId(userId, pageRequest);
                break;
            case PAST:
                bookings = repository.findAllByBookerIdAndEndBefore(userId, now, pageRequest);
                break;
            case WAITING:
                bookings = repository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = repository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;
            case FUTURE:
                bookings = repository.findAllByBookerIdAndStartAfter(userId, now, pageRequest);
                break;
            case CURRENT:
                bookings = repository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, pageRequest);
                break;
            default:
                throw new UnsupportedBookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    public List<BookingOutDto> findBookingsOfOwner(BookingState state, Long userId, int from, int size) {
        UserDto userDto = userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        Sort sort = Sort.by("start").descending();
        Pageable pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        switch (state) {
            case ALL:
                bookings = repository.findByItemOwnerId(userId, pageRequest);
                break;
            case PAST:
                bookings = repository.findByItemOwnerIdAndEndBefore(userId, now, pageRequest);
                break;
            case WAITING:
                bookings = repository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = repository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageRequest);
                break;
            case FUTURE:
                bookings = repository.findByItemOwnerIdAndStartAfter(userId, now, pageRequest);
                break;
            case CURRENT:
                bookings = repository.findByItemOwnerIdAndStartBeforeAndEndAfter(userId, now, now, pageRequest);
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
