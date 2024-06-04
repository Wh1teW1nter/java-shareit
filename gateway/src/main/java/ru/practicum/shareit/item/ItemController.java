package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid ItemCreateDto itemDto) {
        log.info("Creating item bu user {}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                     @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get all items by owner {}", userId);
        return itemClient.getAllItems(userId, from, size);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemUpdateDto itemDto,
                                             @PathVariable Long itemId) {
        log.info("Updating item with id {} by user {}", itemId, userId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find item with id {} by user {}", itemId, userId);
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchItemByText(@NotNull @RequestParam String text,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Search item by text by user {}", userId);
        return itemClient.findItemByText(text, userId, from, size);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Add comment by user {}", userId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
