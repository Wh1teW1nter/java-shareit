package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User owner;
    private User requester;
    private Item item;
    private ItemRequest itemRequest;
    private UserDto userDto;
    private LocalDateTime created;

    @BeforeEach
    public void initialize() {
        created = LocalDateTime.now();
        owner = new User(1L, "owner", "owner@mail.ru");
        requester = new User(2L, "user", "user@mail.ru");
        itemRequest = new ItemRequest(1L, "request", requester, LocalDateTime.now());
        item = new Item(1L, "item", "description", true, owner, itemRequest);
    }

    @Test
    public void createFailUserNotFound() {
        long userId = 1;
        ItemRequestInDto requestInDto = new ItemRequestInDto("request");
        ItemRequest request = ItemRequestMapper.toItemRequest(requestInDto);
        request.setCreated(created);
        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException(""));

        assertThrows(UserNotFoundException.class, () -> itemRequestService.create(requestInDto, userId));
    }

    @Test
    public void getUserRequestsWithAnswersSuccessfulTest() {
        long userId = 2;
        when(userService.findUserById(userId)).thenReturn(UserMapper.toUserDto(requester));
        when(repository.findByRequesterId(userId)).thenReturn(List.of(itemRequest));
        lenient().when(itemRepository.getByRequestIdIn(List.of(1L))).thenReturn(List.of(item));

        List<ItemRequestOutDto> requests = itemRequestService.getUserRequestsWithAnswers(userId);

        assertEquals(1, requests.size());
    }

    @Test
    public void getUserRequestsWithAnswersFailUserNotFound() {
        long userId = 2;
        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException(""));

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getUserRequestsWithAnswers(userId));
    }

    @Test
    public void getRequestsOfOthersSuccessfulTest() {
        long userId = 1;
        int from = 0;
        int size = 10;
        Sort sort = Sort.by("created").descending();
        Pageable pageRequest = PageRequest.of(from, size, sort);
        when(userService.findUserById(userId)).thenReturn(UserMapper.toUserDto(owner));
        lenient().when(repository.findByRequesterIdNot(userId, pageRequest)).thenReturn(List.of(itemRequest));
        lenient().when(itemRepository.getByRequestIdIn(List.of(1L))).thenReturn(List.of(item));

        List<ItemRequestOutDto> requests = itemRequestService.getRequestsOfOthers(userId, from, size);

        assertEquals(1, requests.size());
    }

    @Test
    public void getRequestsOfOthersFailUserNotFound() {
        long userId = 1;
        int from = 0;
        int size = 10;
        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException(""));

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequestsOfOthers(userId, from, size));
    }

    @Test
    public void getItemRequestSuccessfulTest() {
        long userId = 2;
        long requestId = 1;
        when(userService.findUserById(userId)).thenReturn(UserMapper.toUserDto(requester));
        when(repository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        lenient().when(itemRepository.getByRequestIdIn(List.of(requestId))).thenReturn(List.of(item));

        ItemRequestOutDto outDto = itemRequestService.getItemRequest(userId, requestId);

        assertNotNull(outDto);
        assertEquals(1, outDto.getId());
    }

    @Test
    public void getItemRequestFailUserNotFound() {
        long userId = 2;
        long requestId = 1;
        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException(""));

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getItemRequest(userId, requestId));
    }

    @Test
    public void getItemRequestFailRequestNotFound() {
        long userId = 2;
        long requestId = 1;
        when(userService.findUserById(userId)).thenReturn(UserMapper.toUserDto(requester));
        when(repository.findById(requestId)).thenThrow(new ItemRequestNotFoundException(""));

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequest(userId, requestId));
    }

    @Test
    public void findRequestByIdTestSuccessful() {
        long requestId = 1;
        when(repository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequest request = itemRequestService.findRequestById(requestId);

        assertNotNull(request);
        assertEquals(1, request.getId());

    }

    @Test
    public void findRequestByIdTestFailRequestNotFound() {
        long requestId = 1;
        when(repository.findById(requestId)).thenThrow(new ItemRequestNotFoundException(""));

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.findRequestById(requestId));
    }
}
