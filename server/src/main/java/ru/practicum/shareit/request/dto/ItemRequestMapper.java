package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestInDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static ItemRequestOutDto toItemRequestOutDtoWithAnswers(ItemRequest itemRequest,
                                                                   List<ItemForRequestDto> items) {
        return ItemRequestOutDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
