package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingViewDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.exception.BusinessObjectNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemViewDto> getAllItemsByOwner(Long id) {
        Map<Long, Item> itemMap = repository.findAllByOwnerId(id)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, List<BookingViewDto>> bookingMap = bookingRepository.findByItemIdIn(itemMap.keySet())
                .stream()
                .map(BookingMapper::toBookingViewDto)
                .collect(Collectors.groupingBy(BookingViewDto::getItemId));

        Map<Long, List<CommentDto>> commentMap = commentRepository.findByItemIdIn(itemMap.keySet())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        return itemMap.values()
                .stream()
                .map(item -> ItemMapper.toItemViewForOwnerDto(item,
                        bookingMap.getOrDefault(item.getId(), Collections.emptyList()),
                        commentMap.getOrDefault(item.getId(), Collections.emptyList())
                        ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long id) {
        UserDto userDto = userService.findUserById(id);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        Item itemToUpdate = findItemByIdFromRepository(itemId);
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
            return ItemMapper.toItemDto(repository.save(itemToUpdate));
        }
        log.warn("update of item with id {} failed", itemId);
        throw new ItemValidationException("Unable to update empty parameters of item");
    }

    @Override
    public ItemViewDto findItemById(Long itemId, Long userId) {
        Item item = findItemByIdFromRepository(itemId);
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        List<BookingViewDto> bookings = bookingRepository.findByItemId(itemId)
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .map(BookingMapper::toBookingViewDto)
                .collect(Collectors.toList());

        if (userId.equals(item.getOwner().getId())) {
            return ItemMapper.toItemViewForOwnerDto(item,
                    bookings,
                    comments);
        }
        return ItemMapper.toItemViewForBookerDto(item, comments);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.mapToItemDto(repository.searchItemByText(text)
                .stream().filter(Item::getAvailable).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        LocalDateTime commentCreated = LocalDateTime.now();
        Item item = findItemByIdFromRepository(itemId);
        User user = UserMapper.toUser(userService.findUserById(userId));
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, commentCreated);
        bookings = bookings
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .collect(Collectors.toList());

        if (!bookings.isEmpty()) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(commentCreated);
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        }
        throw new CommentValidationException("User cannot add comment to an item without booking");
    }

    private Item findItemByIdFromRepository(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new BusinessObjectNotFoundException("Item was not found"));
    }
}
