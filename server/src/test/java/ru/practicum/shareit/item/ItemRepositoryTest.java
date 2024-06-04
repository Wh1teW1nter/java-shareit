package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private final Pageable pageRequest = PageRequest.of(0, 10);

    @BeforeEach
    public void createItems() {
        User owner = new User(1L, "owner", "owner@mail.ru");
        User user = new User(2L, "user", "user@mail.ru");
        ItemRequest request = new ItemRequest(1L, "request", user, LocalDateTime.now());
        Item item = new Item(1L, "item", "description", true, owner, request);

        userRepository.save(owner);
        userRepository.save(user);
        requestRepository.save(request);
        itemRepository.save(item);
    }

    @Test
    public void findAllByOwnerIdTest() {
        List<Item> items = itemRepository.findAllByOwnerId(1L, pageRequest);

        assertEquals(1, items.size());
    }

    @Test
    public void findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrueTest() {
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue("item",
                "item", pageRequest);

        assertEquals(1, items.size());
    }

    @Test
    public void getByRequestIdInTest() {
        List<Long> requestIds = List.of(1L);
        List<Item> items = itemRepository.getByRequestIdIn(requestIds);

        assertEquals(1, items.size());
    }
}
