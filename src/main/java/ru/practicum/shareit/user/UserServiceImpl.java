package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.mapToUserDto(userStorage.getAllUsers());
    }


    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new UserValidationException("Email must not be null");
        }
        if (userStorage.doesEmailNotExist(userDto.getEmail())) {
            User user = userStorage.create(UserMapper.toUser(userDto));
            return UserMapper.toUserDto(user);
        }
        log.warn("creation of user with email {} failed", userDto.getEmail());
        throw new EmailConflictException("User email is already registered");
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        User oldUser = userStorage.findUserById(id);
        User newUser = new User(oldUser.getId(), oldUser.getName(), oldUser.getEmail());
        boolean updated = false;
        if (userDto.getEmail() != null && userStorage.doesEmailNotExist(userDto.getEmail())
                || Objects.equals(userDto.getEmail(), newUser.getEmail())) {
            newUser.setEmail(userDto.getEmail());
            updated = true;
        }
        if (userDto.getName() != null) {
            newUser.setName(userDto.getName());
            updated = true;
        }
        if (updated) {
            return UserMapper.toUserDto(userStorage.update(oldUser, newUser, id));
        }
        log.warn("update of user with id {} failed", id);
        throw new EmailConflictException("Unable to update user with given email");
    }

    @Override
    public UserDto findUserById(Long id) {
        return UserMapper.toUserDto(userStorage.findUserById(id));
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }
}
