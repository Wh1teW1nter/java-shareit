package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;

    private User owner;
    private User user;
    private Item item;
    private Comment comment;
    private ItemRequest request;
    private Booking booking;

    @BeforeEach
    public void initialize() {
        owner = new User(1L, "owner", "owner@mail.ru");
        user = new User(2L, "user", "user@mail.ru");
        request = new ItemRequest(1L, "request", user, LocalDateTime.now());
        item = new Item(1L, "item", "description", true, null, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED);
        comment = new Comment(1L, "text", item, user, LocalDateTime.now());
    }

    @Test
    void getAllItemsByOwnerWhenDbIsEmptyThenReturnEmptyList() {
        int from = 0;
        int size = 10;
        userRepository.save(owner);

        List<ItemViewDto> dtos = itemService.getAllItemsByOwner(owner.getId(), from, size);

        assertTrue(dtos.isEmpty());
    }

    @Test
    void getAllItemsWhenDbHasOneItemThenReturnList() {
        int from = 0;
        int size = 10;
        userRepository.save(owner);
        itemService.create(ItemMapper.toItemDto(item), owner.getId());

        List<ItemViewDto> result = itemService.getAllItemsByOwner(owner.getId(), from, size);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void createItemSuccessful() {
        userRepository.save(owner);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        ItemDto result = itemService.create(itemDto, owner.getId());

        assertEquals(1L, result.getId());
        assertEquals(itemDto.getName(), result.getName());
    }

    @Test
    void createItemWithRequestSuccessful() {
        userRepository.save(owner);
        userRepository.save(user);
        requestRepository.save(request);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setRequestId(request.getId());

        ItemDto result = itemService.create(itemDto, owner.getId());

        assertEquals(1L, result.getRequestId());
        assertEquals(1L, result.getId());
        assertEquals(itemDto.getName(), result.getName());
    }

    @Test
    void updateItemSuccessful() {
        userRepository.save(owner);
        item.setOwner(owner);
        itemRepository.save(item);
        ItemDto itemUpdate = ItemDto.builder()
                .id(1L)
                .name("update")
                .description("description")
                .available(false)
                .build();

        ItemDto result = itemService.update(itemUpdate, item.getId(), owner.getId());

        assertEquals("update", result.getName());
        assertEquals(false, result.getAvailable());
    }

    @Test
    void updateItemWithUserNotTheOwnerFail() {
        userRepository.save(owner);
        userRepository.save(user);
        item.setOwner(owner);
        itemRepository.save(item);
        ItemDto itemUpdate = ItemDto.builder()
                .id(1L)
                .name("update")
                .description("description")
                .available(false)
                .build();

        assertThrows(NotOwnerForbiddenException.class, () -> itemService.update(itemUpdate, item.getId(), user.getId()));
    }

    @Test
    void updateItemWithEmptyUpdateFail() {
        userRepository.save(owner);
        item.setOwner(owner);
        itemRepository.save(item);

        ItemDto itemUpdate = ItemDto.builder().build();

        assertThrows(ItemValidationException.class, () -> itemService.update(itemUpdate, item.getId(), owner.getId()));
    }

    @Test
    void findItemByIdSuccessful() {
        userRepository.save(owner);
        item.setOwner(owner);
        itemRepository.save(item);

        ItemViewDto result = itemService.findItemById(item.getId(), owner.getId());

        assertEquals(1L, result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getAvailable(), result.getAvailable());
    }

    @Test
    void findItemByIdWithNotRegisteredUserFail() {
        userRepository.save(owner);
        item.setOwner(owner);
        itemRepository.save(item);

        ItemDto itemUpdate = ItemDto.builder()
                .id(1L)
                .name("update")
                .description("description")
                .available(false)
                .build();

        assertThrows(UserNotFoundException.class, () -> itemService.update(itemUpdate, item.getId(), user.getId()));
    }

    @Test
    void findItemByIdItemNotFoundFail() {
        userRepository.save(owner);

        ItemDto itemUpdate = ItemDto.builder()
                .id(1L)
                .name("update")
                .description("description")
                .available(false)
                .build();


        assertThrows(ItemNotFoundException.class, () -> itemService.update(itemUpdate, item.getId(), owner.getId()));
    }

    @Test
    void deleteItem() {
        userRepository.save(owner);
        item.setOwner(owner);
        itemRepository.save(item);

        assertEquals(1, itemRepository.findAll().size());

        itemService.delete(item.getId());

        assertTrue(itemRepository.findAll().isEmpty());
    }

    @Test
    void searchItemByTextReturnEmptyList() {
        userRepository.save(owner);
        item.setOwner(owner);
        itemRepository.save(item);

        List<ItemDto> result = itemService.searchItemByText("", 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemByTextSuccessful() {
        userRepository.save(owner);
        item.setOwner(owner);
        itemRepository.save(item);

        List<ItemDto> result = itemService.searchItemByText("item", 0, 10);

        assertEquals("item", result.get(0).getName());
        assertEquals("description", result.get(0).getDescription());
    }

    @Test
    void addCommentSuccessful() {
        userRepository.save(owner);
        userRepository.save(user);
        item.setOwner(owner);
        itemRepository.save(item);
        bookingRepository.save(booking);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        CommentDto result = itemService.addComment(commentDto, item.getId(), user.getId());

        assertEquals("text", result.getText());
        assertEquals(1L, result.getId());
    }

    @Test
    void addCommentWithItemNotFoundFail() {
        userRepository.save(owner);
        userRepository.save(user);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertThrows(ItemNotFoundException.class,
                () -> itemService.addComment(commentDto, item.getId(), user.getId()));
    }

    @Test
    void addCommentWithUserNotFoundFail() {
        userRepository.save(owner);
        item.setOwner(owner);
        itemRepository.save(item);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertThrows(UserNotFoundException.class,
                () -> itemService.addComment(commentDto, item.getId(), user.getId()));
    }

    @Test
    void addCommentWithoutBookingFail() {
        userRepository.save(owner);
        userRepository.save(user);
        item.setOwner(owner);
        itemRepository.save(item);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertThrows(CommentValidationException.class,
                () -> itemService.addComment(commentDto, item.getId(), user.getId()));
    }
}