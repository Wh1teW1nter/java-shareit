package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private final Pageable pageRequest = PageRequest.of(0, 10);
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    public void createBookings() {
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusDays(1);
        User owner = new User(1L, "owner", "owner@mail.ru");
        User user = new User(2L, "user", "user@mail.ru");
        Item item = new Item(1L, "item", "description", true, owner, null);
        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        repository.save(booking);
    }

    @Test
    void findAllByBookerIdAndStatusTest() {
        List<Booking> bookings = repository.findAllByBookerIdAndStatus(2L, BookingStatus.WAITING, pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdTest() {
        List<Booking> bookings = repository.findAllByBookerId(2L, pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdAndEndBeforeTest() {
        List<Booking> bookings = repository.findAllByBookerIdAndEndBefore(2L, end.plusDays(1), pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStartAfterTest() {
        List<Booking> bookings = repository.findAllByBookerIdAndStartAfter(2L, start.minusDays(1), pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterTest() {
        List<Booking> bookings = repository.findByBookerIdAndStartBeforeAndEndAfter(2L, start.plusHours(1),
                end.minusHours(1), pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdTest() {
        List<Booking> bookings = repository.findByItemOwnerId(1L, pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdAndStatusTest() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStatus(1L, BookingStatus.WAITING, pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdAndEndBeforeTest() {
        List<Booking> bookings = repository.findByItemOwnerIdAndEndBefore(1L, end.plusDays(1), pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdAndStartAfterTest() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStartAfter(1L, start.minusDays(1), pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdAndStartBeforeAndEndAfterTest() {
        List<Booking> bookings = repository.findByItemOwnerIdAndStartBeforeAndEndAfter(1L, start.plusHours(1),
                end.minusHours(1), pageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemIdTest() {
        List<Booking> bookings = repository.findByItemId(1L);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemIdInTest() {
        List<Long> itemIds = List.of(1L);
        List<Booking> bookings = repository.findByItemIdIn(itemIds);

        assertEquals(1, bookings.size());
    }

    @Test
    void findByBookerIdAndItemIdAndEndBeforeTest() {
        List<Booking> bookings = repository.findAllByBookerIdAndEndBefore(2L, end.plusHours(1), pageRequest);

        assertEquals(1, bookings.size());
    }
}
