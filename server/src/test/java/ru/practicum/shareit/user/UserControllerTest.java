package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("test")
            .email("test@mail.ru")
            .build();

    @SneakyThrows
    @Test
    public void testCreateUserSuccess() {
        when(userService.create(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    public void testGetAllUsers() {
        List<UserDto> dtos = List.of(userDto);
        when(userService.getAllUsers()).thenReturn(dtos);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getAllUsers();
        assertEquals(objectMapper.writeValueAsString(dtos), result);
    }

    @SneakyThrows
    @Test
    public void testUpdateUserSuccess() {
        when(userService.update(userDto, userDto.getId())).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userDto.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).update(userDto, userDto.getId());
    }

    @SneakyThrows
    @Test
    public void testFindUserById() {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService).findUserById(userId);
    }

    @SneakyThrows
    @Test
    public void testDeleteUser() {
        mockMvc.perform(delete("/users/{userId}", userDto.getId()))
                .andExpect(status().isOk());

        verify(userService).delete(userDto.getId());
    }
}
