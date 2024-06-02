package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookingMapper {

    public static BookingResponse toBookingDto(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingResponse> mapToBookingDto(Collection<Booking> bookings) {
        List<BookingResponse> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDto(booking));
        }
        return dtos;
    }

    public static Booking toBooking(BookingRequest bookingDto, Item item, User booking, BookingStatus status) {
        Booking newBooking = new Booking();
        newBooking.setId(bookingDto.getId());
        newBooking.setStart(bookingDto.getStart());
        newBooking.setEnd(bookingDto.getEnd());
        newBooking.setItem(item);
        newBooking.setBooker(booking);
        newBooking.setStatus(status);
        return newBooking;
    }

    public static BookingViewDto toBookingViewDto(Booking booking) {
        return BookingViewDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .build();
    }
}
