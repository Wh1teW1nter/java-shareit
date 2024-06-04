package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BusinessObjectNotFoundException;
import ru.practicum.shareit.exception.NotOwnerForbiddenException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemViewDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    private User owner;
    private User user;
    private Item item;
    private Comment comment;
    private Booking bookingLast;
    private Booking bookingNext;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private ItemRequest request;

    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestService itemRequestService;

    @BeforeEach
    public void initialize() {
        owner = new User(1L, "owner", "owner@mail.ru");
        user = new User(2L, "user", "user@mail.ru");
        request = new ItemRequest(1L, "request", user, LocalDateTime.now());
        item = new Item(1L, "item", "description", true, owner, request);
        comment = new Comment(1L, "text", item, user, LocalDateTime.now());
        bookingLast = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1),
                item, user, BookingStatus.WAITING);
        bookingNext = new Booking();
        itemDto = new ItemDto(1L, "item", "description", true, request.getId());
        commentDto = new CommentDto(1L, "comment", item.getId(), user.getId(), user.getName(), LocalDateTime.now());
    }

    @Test
    public void getAllItemsWhenUserNotFoundThenThrowUserNotFoundException() {
        long ownerId = 1L;
        int from = 0;
        int size = 10;
        when(userService.findUserById(ownerId)).thenThrow(new BusinessObjectNotFoundException("User was not found"));

        assertThrows(BusinessObjectNotFoundException.class, () -> itemService.getAllItemsByOwner(ownerId, from, size));
    }

    @Test
    public void getAllItemsWithoutBookingsThenReturnItemWithLastAndNextBookingsAreNull() {
        long ownerId = 1L;
        int from = 0;
        int size = 10;
        Pageable pageRequest = PageRequest.of(from, size);
        when(userService.findUserById(ownerId)).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.findAllByOwnerId(ownerId, pageRequest)).thenReturn(List.of(item));

        List<ItemViewDto> dtos = itemService.getAllItemsByOwner(ownerId, from, size);

        verify(itemRepository).findAllByOwnerId(ownerId, pageRequest);
        assertEquals(1, dtos.size());
        assertNull(dtos.get(0).getLastBooking());
        assertNull(dtos.get(0).getNextBooking());
    }

    @Test
    public void getAllItemsWithBookingsThenReturnItemWithBookings() {
        long ownerId = 1L;
        int from = 0;
        int size = 10;
        Pageable pageRequest = PageRequest.of(from, size);
        when(userService.findUserById(ownerId)).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.findAllByOwnerId(ownerId, pageRequest)).thenReturn(List.of(item));
        lenient().when(bookingRepository.findByItemIdIn(List.of(item.getId()))).thenReturn(List.of(bookingLast, bookingNext));
        lenient().when(commentRepository.findByItemIdIn(List.of(item.getId()))).thenReturn(List.of(comment));

        List<ItemViewDto> dtos = itemService.getAllItemsByOwner(ownerId, from, size);

        verify(itemRepository).findAllByOwnerId(ownerId, pageRequest);
        assertEquals(1, dtos.size());
    }

    @Test
    public void createItemWhenUserNotFoundThenThrowUserNotFoundException() {
        long ownerId = 1L;
        when(userService.findUserById(ownerId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> itemService.create(itemDto, ownerId));
    }

    @Test
    public void createItemThenReturnItemDto() {
        UserDto userDto = UserMapper.toUserDto(owner);
        when(userService.findUserById(owner.getId())).thenReturn(userDto);
        when(itemRequestService.findRequestById(request.getId())).thenReturn(request);
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto dto = itemService.create(ItemMapper.toItemDto(item), owner.getId());

        verify(itemRepository).save(item);
    }

    @Test
    public void updateItemWhenUserNotFoundThenThrowUserNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userService.findUserById(ownerId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> itemService.update(itemDto, itemId, ownerId));
    }

    @Test
    public void updateItemWhenItemNotFoundThenThrowItemNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userService.findUserById(ownerId)).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.findById(itemId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> itemService.update(itemDto, itemId, ownerId));
    }

    @Test
    public void updateItemWhenUserNotOwnerThenThrowNotOwnerForbiddenException() {
        long userId = 2L;
        long itemId = 1L;
        when(userService.findUserById(userId)).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotOwnerForbiddenException.class, () -> itemService.update(itemDto, itemId, userId));
    }

    @Test
    public void updateItemThenReturnUpdatedItemDto() {
        long ownerId = 1L;
        long itemId = 1L;
        ItemDto dto = itemDto;
        dto.setDescription("update");
        when(userService.findUserById(ownerId)).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto result = itemService.update(dto, itemId, ownerId);

        assertNotNull(result);
        assertEquals("update", result.getDescription());
    }

    @Test
    public void findItemByIdWhenItemNotFoundThenThrowItemNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userService.findUserById(ownerId)).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.findById(itemId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> itemService.findItemById(itemId, ownerId));
    }

    @Test
    public void findItemByIdWhenOwnerNotFoundThenThrowItemNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;

        when(userService.findUserById(ownerId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> itemService.findItemById(itemId, ownerId));
    }

    @Test
    public void findItemByIdForOwnerThenReturnItemViewForOwnerDto() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userService.findUserById(ownerId)).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));
        when(bookingRepository.findByItemId(itemId)).thenReturn(List.of(bookingLast));

        ItemViewDto itemViewDto = itemService.findItemById(itemId, ownerId);

        assertEquals(1, itemViewDto.getId());
        assertFalse(itemViewDto.getComments().isEmpty());
    }

    @Test
    public void deleteItem() {
        long itemId = 1L;

        itemService.delete(itemId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    public void searchItemByNameWhenTextIsBlankThenReturnEmptyList() {
        int from = 0;
        int size = 10;
        String text = "";

        List<ItemDto> result = itemService.searchItemByText(text, from, size);

        assertTrue(result.isEmpty());
    }

    @Test
    public void searchItemByNameThenReturnItemDto() {
        int from = 0;
        int size = 10;
        String text = "item";
        Pageable pageRequest = PageRequest.of(from, size);
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text, pageRequest))
                .thenReturn(List.of(item));

        List<ItemDto> result = itemService.searchItemByText(text, from, size);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    public void addCommentWhenUserNotFoundThenThrowUserNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.findUserById(ownerId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> itemService.addComment(commentDto, itemId, ownerId));
    }

    @Test
    public void addCommentWhenItemNotFoundThenThrowItemNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;

        when(itemRepository.findById(itemId)).thenThrow(new BusinessObjectNotFoundException(""));

        assertThrows(BusinessObjectNotFoundException.class, () -> itemService.addComment(commentDto, itemId, ownerId));
    }
}
