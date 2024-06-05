package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User owner;
    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    public void initialize() {
        owner = new User(1L, "owner", "owner@mail.ru");
        user = new User(2L, "user", "user@mail.ru");
        item = new Item(1L, "item", "description", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(2), item, user, BookingStatus.WAITING);
        bookingDto = new BookingDto(1L, 1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), null);
    }

    @Test
    public void createBookingTest() {
        long userId = 2L;
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);

        BookingOutDto result = bookingService.create(bookingDto, userId);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getItem().getId());
    }

    @Test
    public void setBookingApprovalTest() {
        long userId = 1L;
        long bookingId = 1L;
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        BookingOutDto bookingOutDto = bookingService.setBookingApproval(userId, true, bookingId);

        assertEquals(1, bookingOutDto.getId());
        assertEquals(BookingStatus.APPROVED, bookingOutDto.getStatus());
    }

    @Test
    public void findBookingByIdTest() {
        long userId = 1L;
        long bookingId = 1L;
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        BookingOutDto bookingOutDto = bookingService.findBookingById(bookingId, userId);

        assertEquals(bookingId, bookingOutDto.getId());
    }

    @Test
    public void findBookingsOfUserTest() {
        long bookingId = 1L;
        long userId = 2L;
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<BookingOutDto> bookings = bookingService.findBookingsOfUser(BookingState.ALL, userId, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(bookingId, bookings.get(0).getId());
    }

    @Test
    public void findBookingsOfOwnerTest() {
        long bookingId = 1L;
        long userId = 1L;
        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<BookingOutDto> bookings = bookingService.findBookingsOfOwner(BookingState.ALL, userId, 0, 10);

        assertEquals(1, bookings.size());
        assertEquals(bookingId, bookings.get(0).getId());
    }
}
