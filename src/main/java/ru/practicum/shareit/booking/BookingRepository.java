package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long userId,
                                                          LocalDateTime start,
                                                          LocalDateTime end,
                                                          Pageable pageRequest);

    List<Booking> findByItemOwnerId(Long userId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBefore(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfter(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long userId,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             Pageable pageRequest);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdIn(Collection<Long> itemIds);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime end);
}
