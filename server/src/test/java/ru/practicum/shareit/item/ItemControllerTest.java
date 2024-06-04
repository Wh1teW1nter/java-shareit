package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(false)
            .build();

    @SneakyThrows
    @Test
    public void getAllItemsByOwnerSuccessful() {
        ItemViewDto itemViewDto = ItemViewDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(false)
                .comments(Collections.emptyList())
                .build();

        when(itemService.getAllItemsByOwner(1L, 0, 10)).thenReturn(List.of(itemViewDto));

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemViewDto)), result);
    }

    @Test
    @SneakyThrows
    void findItemByIdSuccessfulThenReturnItemDto() {
        long userId = 1L;
        long itemId = 2L;
        ItemViewDto itemViewDto = ItemViewDto.builder().build();
        when(itemService.findItemById(itemId, userId)).thenReturn(itemViewDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemViewDto), result);
    }

    @SneakyThrows
    @Test
    void getAllItemsWhenWithoutSizeParamThenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;
        List<ItemViewDto> items = List.of(ItemViewDto.builder().build());
        when(itemService.getAllItemsByOwner(userId, 2, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getAllItemsByOwner(userId, 2, 10);
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void searchItemByTextWhenWithoutParamsThenStatusOkAndParamIsDefault() {
        long userId = 1L;
        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItemByText("item", 0, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItemByText("item", 0, 10);
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void searchItemByTextWhenWithoutSizeParamThenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;
        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItemByText("item", 2, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItemByText("item", 2, 10);
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void searchItemByTextWhenWithParamsThenStatusOk() {
        long userId = 1L;
        String search = "item";
        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItemByText("item", 2, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("size", String.valueOf(10))
                        .param("text", search))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItemByText("item", 2, 10);
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void createItemSuccessfulThenReturnStatusOk() {
        long userId = 1L;
        when(itemService.create(itemDto, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void addCommentSuccessfulThenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 2L;
        CommentDto comment = CommentDto.builder()
                .text(" ")
                .build();
        CommentDto commentOutDto = CommentDto.builder()
                .text("text")
                .authorName("name")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(comment, itemId, userId)).thenReturn(commentOutDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentOutDto), result);
    }

    @SneakyThrows
    @Test
    void updateItemSuccessfulThenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 2L;
        when(itemService.update(itemDto, itemId, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }
}
