package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestCreateDto itemRequestInDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating request by user {}", userId);
        return itemRequestClient.createRequest(itemRequestInDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequestsWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get user`s requests with id {}", userId);
        return itemRequestClient.getUserRequestsWithAnswers(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsOfOthers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                      @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get requests of others by user {}", userId);
        return itemRequestClient.getRequestsOfOthers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Get request {} by user {}", requestId, userId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
