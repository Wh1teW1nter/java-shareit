package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItemsByOwner(Long id);

    ItemDto create(ItemDto itemDto, Long id);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto findItemById(Long id);

    void delete(Long id);

    List<ItemDto> searchItemByText(String text);
}
