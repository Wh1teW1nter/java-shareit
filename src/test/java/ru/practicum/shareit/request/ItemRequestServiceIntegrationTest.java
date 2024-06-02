package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegrationTest {

    private final ItemRequestRepository repository;
    private final ItemRequestService itemRequestService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private User owner;
    private User requester;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    public void initialize() {
        requester = new User(1L, "user", "user@mail.ru");
        owner = new User(2L, "owner", "owner@mail.ru");
        request = new ItemRequest(1L, "request", requester, LocalDateTime.now());
        item = new Item(1L, "item", "description", true, owner, request);
    }

    @Test
    public void createItemRequestTest() {
        long requesterId = 1;
        userRepository.save(requester);
        ItemRequestInDto inDto = new ItemRequestInDto("request");

        ItemRequestDto outDto = itemRequestService.create(inDto, requesterId);

        assertEquals(1L, outDto.getId());
        assertEquals("request", outDto.getDescription());
    }

    @Test
    public void getUserRequestsWithAnswersTest() {
        long requesterId = 1;
        userRepository.save(requester);
        repository.save(request);
        userRepository.save(owner);
        itemRepository.save(item);

        List<ItemRequestViewDto> dtos = itemRequestService.getUserRequestsWithAnswers(requesterId);

        assertEquals(1, dtos.size());
        assertEquals("item", dtos.get(0).getItems().get(0).getName());
    }

    @Test
    public void getRequestsOfOthersTest() {
        long userId = 2;
        userRepository.save(requester);
        repository.save(request);
        userRepository.save(owner);
        itemRepository.save(item);

        List<ItemRequestViewDto> dtos = itemRequestService.getRequestsOfOthers(userId, 0, 10);

        assertEquals(1, dtos.size());
        assertEquals("item", dtos.get(0).getItems().get(0).getName());
    }

    @Test
    public void getItemRequestTest() {
        long userId = 1;
        long requestId = 1;
        userRepository.save(requester);
        repository.save(request);

        ItemRequestViewDto itemRequestOutDto = itemRequestService.getItemRequest(1L, requestId);

        assertEquals(1, itemRequestOutDto.getId());
    }
}
