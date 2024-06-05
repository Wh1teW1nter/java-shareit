package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestInDto itemRequestInDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.create(itemRequestInDto, userId);
    }

    @GetMapping
    public List<ItemRequestOutDto> getUserRequestsWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getUserRequestsWithAnswers(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getRequestsOfOthers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return requestService.getRequestsOfOthers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long requestId) {
        return requestService.getItemRequest(userId, requestId);
    }
}
