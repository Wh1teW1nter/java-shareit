package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemViewDto> getAllItemsViewDtoByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("пришел GET запрос /items с userId: {}", userId);
        List<ItemViewDto> allItems = itemService.getAllItemsByOwner(userId);
        log.info("отправлен ответ на GET запрос /items с userId: {} с телом: {}", userId, allItems);
        return allItems;
    }

    @PostMapping
    public ItemDto itemDtoCreateResponse(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("пришел POST запрос /items с userId: {} и itemDto: {}", userId, itemDto);
        ItemDto responseItemDto = itemService.create(itemDto, userId);
        log.info("отправлен ответ на POST запрос /items с userId: {} и itemDto: {} с телом: {}", userId, itemDto, responseItemDto);
        return responseItemDto;
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto itemDtoUpdateResponse(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("пришел PATCH запрос /items с userId: {} и itemDto: {}", userId, itemDto);
        ItemDto responseItemDto = itemService.update(itemDto, itemId, userId);
        log.info("отправлен ответ на POST запрос /items с userId: {} и itemDto: {} с телом: {}", userId, itemDto, responseItemDto);
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemViewDto findItemViewDtoByIdResponse(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("пришел PATCH запрос /items/:itemId с itemId: {}", itemId);
        ItemViewDto responseItemDto = itemService.findItemById(itemId, userId);
        log.info("отправлен ответ на PATCH запрос /items/:itemId с itemId: {} и с телом: {}", itemId, responseItemDto);
        return responseItemDto;
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItemDtoByTextResponse(@NotNull @RequestParam String text) {
        log.info("пришел GET запрос /items/search с text: {}", text);
        List<ItemDto> responseListItemDto = itemService.searchItemByText(text);
        log.info("отправлен ответ на GET запрос /items/search с text: {} с телом: {}", text, responseListItemDto);
        return responseListItemDto;
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto addCommentResponse(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid CommentDto commentDto) {
        log.info("пришел POST запрос {itemId}/comment с itemId: {} и userId: {}", itemId, commentDto);
        CommentDto responseCommentDto = itemService.addComment(commentDto, itemId, userId);
        log.info("отправлен ответ на POST запрос {itemId}/comment с itemId: {} и userId: {} с телом: {}", itemId, commentDto, responseCommentDto);
        return responseCommentDto;
    }
}
