package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestInDto itemRequestDto, Long userId);

    List<ItemRequestOutDto> getUserRequestsWithAnswers(Long userId);

    List<ItemRequestOutDto> getRequestsOfOthers(Long userId, int from, int size);

    ItemRequestOutDto getItemRequest(Long userId, Long requestId);

    ItemRequest findRequestById(Long itemRequestId);
}
