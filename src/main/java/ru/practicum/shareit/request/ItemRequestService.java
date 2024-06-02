package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestInDto itemRequestDto, Long userId);

    List<ItemRequestViewDto> getUserRequestsWithAnswers(Long userId);

    List<ItemRequestViewDto> getRequestsOfOthers(Long userId, int from, int size);

    ItemRequestViewDto getItemRequest(Long userId, Long requestId);

    ItemRequest findRequestById(Long itemRequestId);
}
