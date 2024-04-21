package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getAllItemsByOwner(Long id);

    Item create(Item item);

    Item update(Item item);

    Item findItemById(Long id);

    void delete(Long id);

    List<Item> searchItemByText(String text);
}
