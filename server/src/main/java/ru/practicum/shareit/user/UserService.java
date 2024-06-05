package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    UserDto findUserById(Long id);

    void delete(Long id);
}
