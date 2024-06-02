package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    public void toUserDtoTest() {
        User user = new User(1L, "user1", "one@ru");
        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void toUserTest() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail")
                .build();

        User user = UserMapper.toUser(dto);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void mapToUserDtoTest() {
        List<User> users = List.of(new User(1L, "user1", "one@ru"),
                new User(2L, "user2", "two@ru"));

        List<UserDto> dtos = UserMapper.mapToUserDto(users);

        assertEquals(2, dtos.size());
    }
}
