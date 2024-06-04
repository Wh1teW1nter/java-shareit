package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestInDto itemRequestInDto, Long userId) {
        User user = UserMapper.toUser(userService.findUserById(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestInDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public List<ItemRequestOutDto> getUserRequestsWithAnswers(Long userId) {
        User user = UserMapper.toUser(userService.findUserById(userId));
        Map<Long, ItemRequest> itemRequestMap = repository.findByRequesterId(userId).stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<Long, List<ItemForRequestDto>> itemsMap = getItemsByRequestIds(itemRequestMap.keySet());
        return itemRequestMap.values()
                .stream()
                .map(itemRequest -> ItemRequestMapper
                        .toItemRequestOutDtoWithAnswers(itemRequest, itemsMap.getOrDefault(itemRequest.getId(),
                                Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestOutDto> getRequestsOfOthers(Long userId, int from, int size) {
        User user = UserMapper.toUser(userService.findUserById(userId));
        Sort sort = Sort.by("created").descending();
        Pageable pageRequest = PageRequest.of(from, size, sort);
        Map<Long, ItemRequest> itemRequestMap = repository.findByRequesterIdNot(userId, pageRequest).stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        Map<Long, List<ItemForRequestDto>> itemsMap = getItemsByRequestIds(itemRequestMap.keySet());
        return itemRequestMap.values()
                .stream()
                .map(itemRequest -> ItemRequestMapper
                        .toItemRequestOutDtoWithAnswers(itemRequest, itemsMap.getOrDefault(itemRequest.getId(),
                                Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestOutDto getItemRequest(Long userId, Long requestId) {
        User user = UserMapper.toUser(userService.findUserById(userId));
        ItemRequest itemRequest = findRequestById(requestId);
        List<ItemForRequestDto> items = itemRepository.getByRequestIdIn(Collections.singleton(requestId))
                .stream()
                .map(ItemMapper::toItemForRequestDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestOutDtoWithAnswers(itemRequest, items);
    }

    @Override
    public ItemRequest findRequestById(Long itemRequestId) {
        return repository.findById(itemRequestId).orElseThrow(() -> new ItemRequestNotFoundException("Item was not found"));
    }

    private Map<Long, List<ItemForRequestDto>> getItemsByRequestIds(Collection<Long> requestIds) {
        List<Item> items = itemRepository.getByRequestIdIn(requestIds);
        return items.stream()
                .map(ItemMapper::toItemForRequestDto)
                .collect(Collectors.groupingBy(ItemForRequestDto::getRequestId));
    }
}
