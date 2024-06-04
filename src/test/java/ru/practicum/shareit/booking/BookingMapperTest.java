package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingViewDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    @Test
    public void toBookingDtoTest() {
        User user = new User(2L, "user", "user@mail.ru");
        Item item = new Item(1L, "item", "description", true, null, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(2), item, user, BookingStatus.WAITING);

        BookingResponse bookingOutDto = BookingMapper.toBookingDto(booking);

        assertEquals(1L, bookingOutDto.getId());
        assertEquals(BookingStatus.WAITING, bookingOutDto.getStatus());
        assertEquals(booking.getStart(), bookingOutDto.getStart());
        assertEquals(booking.getBooker(), bookingOutDto.getBooker());
    }

    @Test
    public void mapToBookingDtoTest() {
        User user = new User(2L, "user", "user@mail.ru");
        Item item = new Item(1L, "item", "description", true, null, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(2), item, user, BookingStatus.WAITING);
        List<Booking> bookings = List.of(booking);

        List<BookingResponse> dtos = BookingMapper.mapToBookingDto(bookings);

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(BookingStatus.WAITING, dtos.get(0).getStatus());
        assertEquals(booking.getStart(), dtos.get(0).getStart());
        assertEquals(booking.getBooker(), dtos.get(0).getBooker());
    }

    @Test
    public void toBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingRequest bookingDto = new BookingRequest(1L, 1L, start, end, BookingStatus.WAITING);

        Booking booking = BookingMapper.toBookingMapper(bookingDto);

        assertEquals(1, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
    }

    @Test
    public void toBookingViewDtoTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        User user = new User(2L, "user", "user@mail.ru");
        Item item = new Item(1L, "item", "description", true, null, null);
        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        BookingViewDto bookingViewDto = BookingMapper.toBookingViewDto(booking);

        assertEquals(1, bookingViewDto.getId());
        assertEquals(start, bookingViewDto.getStart());
        assertEquals(end, bookingViewDto.getEnd());
        assertEquals(2, bookingViewDto.getBookerId());
        assertEquals(1, bookingViewDto.getItemId());
    }
}
