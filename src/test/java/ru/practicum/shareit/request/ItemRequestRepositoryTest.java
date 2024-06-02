package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void createRequests() {
        User user = new User(1L, "user", "user@mail.ru");
        User user1 = new User(2L, "user1", "user1@mail.ru");
        ItemRequest request = new ItemRequest(1L, "request", user, LocalDateTime.now());

        userRepository.save(user);
        userRepository.save(user1);
        itemRequestRepository.save(request);
    }

    @Test
    public void findByRequesterIdTest() {
        List<ItemRequest> requests = itemRequestRepository.findByRequesterId(1L);

        assertEquals(1, requests.size());
    }

    @Test
    public void findByRequesterIdNot() {
        Pageable pageRequest = PageRequest.of(0, 10);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(2L, pageRequest);

        assertEquals(1, requests.size());
        assertEquals(1, requests.get(0).getRequester().getId());
    }
}
