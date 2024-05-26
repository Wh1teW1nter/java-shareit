package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    @ToString.Exclude
    private User booker;
    @Enumerated(value = EnumType.STRING)
    private BookingStatus status;
}
