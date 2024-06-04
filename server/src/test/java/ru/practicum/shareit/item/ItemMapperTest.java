package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingViewDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemMapperTest {

    @Test
    void toItemDto() {
        Item item = new Item(1L, "item", "description", true, null, null);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void mapToItemDto() {
        Item item = new Item(1L, "item", "description", true, null, null);
        Item item2 = new Item(2L, "item2", "description2", false, null, null);
        List<Item> items = List.of(item, item2);

        List<ItemDto> dtos = ItemMapper.mapToItemDto(items);

        assertEquals(2, dtos.size());
    }

    @Test
    void toItem() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(dto);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }

    @Test
    void toItemViewForOwnerDto() {
        Item item = new Item(1L, "item", "description", true, null, null);
        BookingViewDto lastBooking = BookingViewDto.builder()
                .start(LocalDateTime.now().minusDays(1L))
                .build();
        BookingViewDto nextBooking = BookingViewDto.builder()
                .start(LocalDateTime.now().plusDays(1L))
                .build();
        List<BookingViewDto> bookings = List.of(lastBooking, nextBooking);
        List<CommentDto> comments = List.of(CommentDto.builder().build());

        ItemViewDto itemViewDto = ItemMapper.toItemViewForOwnerDto(item, bookings, comments);

        assertEquals("item", itemViewDto.getName());
        assertNotNull(itemViewDto.getLastBooking());
        assertNotNull(itemViewDto.getNextBooking());
        assertEquals(lastBooking, itemViewDto.getLastBooking());
        assertEquals(1, itemViewDto.getComments().size());
    }

    @Test
    void toItemViewForBookerDto() {
        Item item = new Item(1L, "item", "description", true, null, null);
        List<CommentDto> comments = List.of(CommentDto.builder().build());

        ItemViewDto itemViewDto = ItemMapper.toItemViewForBookerDto(item, comments);

        assertEquals("item", itemViewDto.getName());
        assertEquals(1, itemViewDto.getComments().size());
    }

    @Test
    void toItemForRequestDto() {
        User owner = new User(1L, "owner", "owner@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "need a brush", null, LocalDateTime.now());
        Item item = new Item(1L, "item", "description", true, owner, itemRequest);

        ItemForRequestDto itemDto = ItemMapper.toItemForRequestDto(item);

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
        assertEquals(item.getAvailable(), itemDto.isAvailable());
    }
}