package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingViewDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.*;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static List<ItemDto> mapToItemDto(Collection<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemViewDto toItemViewForOwnerDto(Item item, List<BookingViewDto> bookings,
                                                    List<CommentDto> comments) {
        LocalDateTime now = LocalDateTime.now();


        return ItemViewDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(bookings.stream().filter(b -> b.getStart().isBefore(now))
                        .max(Comparator.comparing(BookingViewDto::getStart)).orElse(null))
                .nextBooking(bookings.stream().filter(b -> b.getStart().isAfter(now))
                        .min(Comparator.comparing(BookingViewDto::getStart)).orElse(null))
                .comments(comments)
                .build();
    }

    public static ItemViewDto toItemViewForBookerDto(Item item, List<CommentDto> comments) {
        return ItemViewDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest().getId())
                .available(item.getAvailable())
                .description(item.getDescription())
                .build();
    }
}
