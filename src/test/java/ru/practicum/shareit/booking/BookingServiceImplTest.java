package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User user;
    private Item item;
    private BookingRequest bookingDto;
    private Booking booking;
    private ItemDto itemDto;

    @BeforeEach
    public void initialize() {
        LocalDateTime now = LocalDateTime.now();
        owner = new User(1L, "owner", "owner@mail.ru");
        user = new User(2L, "user", "user@mail.ru");
        item = new Item(1L, "item", "description", true, owner, null);
        bookingDto = new BookingRequest(1L, 1L, now.plusHours(1), now.plusDays(1), BookingStatus.WAITING);
        booking = new Booking(1L, now.plusHours(1), now.plusDays(1), item, user, BookingStatus.WAITING);
        itemDto = new ItemDto(1L, "item", "description", true, null);
    }

    @Test
    public void createSuccessful() {
        long userId = 2;
        long itemId = 1;
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(repository.save(booking)).thenReturn(booking);

        BookingResponse result = bookingService.create(bookingDto, userId);

        verify(repository).save(booking);

    }

    @Test
    public void createFailBookerNotFound() {
        long userId = 2;
        when(userService.findUserById(userId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> bookingService.create(bookingDto, userId));
    }

    @Test
    public void createFailItemNotFound() {
        long itemId = 1;
        long userId = 2;
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> bookingService.create(bookingDto, userId));
    }

    @Test
    public void setBookingApprovalSuccessful() {
        long bookingId = 1;
        long userId = 1;
        UserDto userDto = UserMapper.toUserDto(owner);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(repository.save(booking)).thenReturn(booking);

        bookingService.setBookingApproval(userId, true, bookingId);

        verify(repository).save(booking);
    }

    @Test
    public void setBookingApprovalFailBookingNotFound() {
        long bookingId = 1;
        long userId = 1;
        UserDto userDto = UserMapper.toUserDto(owner);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findById(bookingId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> bookingService.setBookingApproval(userId, true, bookingId));

    }

    @Test
    public void setBookingApprovalFailWhenNotOwnerOfItem() {
        User user1 = new User(3L, "user", "user@mail.ru");
        UserDto userDto = UserMapper.toUserDto(user1);
        long bookingId = 1;
        long userId = 3;

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingValidationException.class, () -> bookingService.setBookingApproval(userId, true, bookingId));
    }

    @Test
    public void setBookingApprovalFailWhenUserIsBooker() {
        long bookingId = 1;
        long userId = 2;
        UserDto userDto = UserMapper.toUserDto(user);

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(UserAccessForbiddenException.class, () -> bookingService.setBookingApproval(userId, true, bookingId));
    }

    @Test
    public void setBookingApprovalFailWhenBookingStatusIsNotWaiting() {
        Booking booking1 = booking;
        booking1.setStatus(BookingStatus.REJECTED);
        long bookingId = 1;
        long userId = 1;
        UserDto userDto = UserMapper.toUserDto(owner);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking1));

        assertThrows(BookingValidationException.class, () -> bookingService.setBookingApproval(userId, true, bookingId));
    }

    @Test
    public void findBookingByIdSuccessfulWhenUserIsOwner() {
        long bookingId = 1;
        long userId = 1;
        UserDto userDto = UserMapper.toUserDto(owner);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponse bookingOutDto = bookingService.findBookingById(bookingId, userId);

        assertEquals(1L, bookingOutDto.getId());
        assertEquals(user.getName(), bookingOutDto.getBooker().getName());
    }

    @Test
    public void findBookingByIdSuccessfulWhenUserIsBooker() {
        long bookingId = 1;
        long userId = 2;
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponse bookingOutDto = bookingService.findBookingById(bookingId, userId);

        assertEquals(1L, bookingOutDto.getId());
        assertEquals(user.getName(), bookingOutDto.getBooker().getName());
    }

    @Test
    public void findBookingByIdFailWhenUserNotFound() {
        long bookingId = 1;
        long userId = 1;
        UserDto userDto = UserMapper.toUserDto(owner);
        when(userService.findUserById(userId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> bookingService.findBookingById(bookingId, userId));
    }

    @Test
    public void findBookingByIdFailWhenBookingNotFound() {
        long bookingId = 1;
        long userId = 2;
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findById(bookingId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> bookingService.findBookingById(bookingId, userId));
    }

    @Test
    public void findBookingsOfUserSuccessful() {
        BookingState state = BookingState.ALL;
        long userId = 2;
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("start").descending();
        Pageable pageRequest = PageRequest.of(0, size, sort);
        UserDto userDto = UserMapper.toUserDto(user);

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findAllByBookerId(userId, pageRequest)).thenReturn(List.of(booking));

        List<BookingResponse> bookings = bookingService.findBookingsOfUser(state, userId, from, size);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findBookingsOfUserFailUserNotFound() {
        BookingState state = BookingState.ALL;
        long userId = 1;
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("start").descending();

        when(userService.findUserById(userId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> bookingService.findBookingsOfUser(state, userId, from, size));
    }

    @Test
    public void findBookingsOfUserFailUnknownState() {
        BookingState state = BookingState.UNSUPPORTED_STATUS;
        long userId = 2;
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("start").descending();
        UserDto userDto = UserMapper.toUserDto(user);

        when(userService.findUserById(userId)).thenReturn(userDto);

        assertThrows(UnsupportedBookingStateException.class, () -> bookingService.findBookingsOfUser(state, userId, from, size));
    }

    @Test
    public void findBookingsOfOwnerSuccessful() {
        BookingState state = BookingState.ALL;
        long userId = 1;
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("start").descending();
        Pageable pageRequest = PageRequest.of(0, size, sort);
        UserDto userDto = UserMapper.toUserDto(owner);

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(repository.findByItemOwnerId(userId, pageRequest)).thenReturn(List.of(booking));

        List<BookingResponse> bookings = bookingService.findBookingsOfOwner(state, userId, from, size);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findBookingsOfOwnerFailUserNotFound() {
        BookingState state = BookingState.ALL;
        long userId = 2;
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("start").descending();
        Pageable pageRequest = PageRequest.of(0, size, sort);

        when(userService.findUserById(userId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> bookingService.findBookingsOfOwner(state, userId, from, size));
    }

    @Test
    public void findBookingsOfOwnerFailUnknownState() {
        BookingState state = BookingState.UNSUPPORTED_STATUS;
        long userId = 2;
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("start").descending();
        UserDto userDto = UserMapper.toUserDto(owner);

        assertThrows(UnsupportedBookingStateException.class, () -> bookingService.findBookingsOfOwner(state, userId, from, size));
    }
}
