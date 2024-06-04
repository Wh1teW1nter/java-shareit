package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.mapToUserDto(repository.findAll());
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new UserValidationException("Email must not be null");
        }
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        User oldUser = UserMapper.toUser(findUserById(id));
        boolean updated = false;
        if (userDto.getEmail() != null && doesEmailNotExist(userDto.getEmail())
                || Objects.equals(userDto.getEmail(), oldUser.getEmail())) {
            oldUser.setEmail(userDto.getEmail());
            updated = true;
        }
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
            updated = true;
        }
        if (updated) {
            return UserMapper.toUserDto(repository.save(oldUser));
        }
        log.warn("update of user with id {} failed", id);
        throw new EmailConflictException("Unable to update user with given email");
    }

    @Override
    public UserDto findUserById(Long id) {
        return UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User was not found")));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException("User to delete was not found");
        }
        repository.deleteById(id);
    }

    private boolean doesEmailNotExist(String email) {
        return !repository.findAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
