package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.NotOwnerForbiddenException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long id) {
        UserDto userDto = userService.findUserById(id);
        return ItemMapper.mapToItemDto(itemStorage.getAllItemsByOwner(id));
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long id) {
        UserDto userDto = userService.findUserById(id);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));
        return ItemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        Item itemToUpdate = itemStorage.findItemById(itemId);

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new NotOwnerForbiddenException("User is not the owner of an item");
        }

        boolean updated = false;
        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
            updated = true;
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
            updated = true;
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
            updated = true;
        }
        if (updated) {
            return ItemMapper.toItemDto(itemToUpdate);
        }
        log.warn("update of item with id {} failed", itemId);
        throw new ItemValidationException("Unable to update empty parameters of item");
    }

    @Override
    public ItemDto findItemById(Long id) {
        return ItemMapper.toItemDto(itemStorage.findItemById(id));
    }

    @Override
    public void delete(Long id) {
        itemStorage.delete(id);
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.mapToItemDto(itemStorage.searchItemByText(text));
    }
}
