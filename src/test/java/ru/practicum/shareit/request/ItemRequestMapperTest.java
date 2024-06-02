package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRequestMapperTest {

    @Test
    public void toItemRequestTest() {
        ItemRequestInDto inDto = new ItemRequestInDto("desc");

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(inDto);

        assertEquals(inDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    public void toItemRequestDtoTest() {
        User user = new User(1L, "user", "user@mail.ru");
        ItemRequest request = new ItemRequest(1L, "request", user, LocalDateTime.now());

        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        assertEquals(1, requestDto.getId());
        assertEquals("request", requestDto.getDescription());
        assertNotNull(requestDto.getCreated());
    }

    @Test
    public void toItemRequestOutDtoWithAnswersTest() {
        User user = new User(1L, "user", "user@mail.ru");
        ItemRequest request = new ItemRequest(1L, "request", user, LocalDateTime.now());
        ItemForRequestDto itemForRequestDto = new ItemForRequestDto(1L, 1L, "item", 3L,
                "description", true);
        List<ItemForRequestDto> answers = List.of(itemForRequestDto);

        ItemRequestViewDto itemRequestOutDto = ItemRequestMapper.toItemRequestOutDtoWithAnswers(request, answers);

        assertEquals(1, itemRequestOutDto.getId());
        assertEquals(1, itemRequestOutDto.getItems().get(0).getId());
        assertEquals("item", itemRequestOutDto.getItems().get(0).getName());
        assertEquals(1, itemRequestOutDto.getItems().size());
    }
}
