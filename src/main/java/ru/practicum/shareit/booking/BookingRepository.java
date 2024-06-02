package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(Long userId,
                                                                          LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                             LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdIn(Collection<Long> itemIds);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime end);
}
