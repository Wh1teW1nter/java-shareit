package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody @Valid ItemRequestInDto itemRequestInDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("пришел POST запрос /requests с userId: {} и itemRequestInDto: {}", userId, itemRequestInDto);
        ItemRequestDto itemResponse = requestService.create(itemRequestInDto, userId);
        log.info("отправлен ответ на POST запрос /requests с userId: {} и itemRequestInDto: {} с телом: {}", userId, itemRequestInDto, itemResponse);
        return itemResponse;
    }

    @GetMapping
    public List<ItemRequestViewDto> getUserRequestsWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("пришел GET запрос /requests с userId: {}", userId);
        List<ItemRequestViewDto> itemResponse = requestService.getUserRequestsWithAnswers(userId);
        log.info("отправлен ответ на GET запрос /requests с userId: {} с телом: {}", userId, itemResponse);
        return itemResponse;
    }

    @GetMapping("/all")
    public List<ItemRequestViewDto> getRequestsOfOthers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                        @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("пришел GET запрос /requests/all с userId: {}, from: {}, size: {}", userId, from, size);
        List<ItemRequestViewDto> itemResponse = requestService.getRequestsOfOthers(userId, from, size);
        log.info("отправлен ответ на GET запрос /requests/all с userId: {}, from: {}, size: {} с телом: {}", userId, from, size, userId, itemResponse);
        return itemResponse;
    }

    @GetMapping("/{requestId}")
    public ItemRequestViewDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long requestId) {
        log.info("пришел GET запрос /requests/:requestId с userId: {} и requestId: {}", userId, requestId);
        ItemRequestViewDto itemResponse = requestService.getItemRequest(userId, requestId);
        log.info("отправлен ответ на GET запрос /requests/:requestId с userId: {} и requestId: {} с телом: {}", userId, requestId, itemResponse);
        return itemResponse;
    }
}
