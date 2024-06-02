package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BusinessObjectNotFoundException;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private final User user = new User(1L, "test", "test@ya.ru");

    @Test
    void findUserByIdWhenUserFoundThenReturnedUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findUserById(1L);
        UserDto expectedResult = UserMapper.toUserDto(user);

        assertEquals(expectedResult, result);
    }

    @Test
    void findUserByIdWhenUserNotFoundThenUserNotFoundExceptionThrown() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessObjectNotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void createNewUserSuccessfulThenReturnUserDto() {
        UserDto expectedUserDto = UserMapper.toUserDto(user);
        expectedUserDto.setId(1L);
        UserDto userDtoToCreate = UserMapper.toUserDto(user);

        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.create(userDtoToCreate);

        assertEquals(expectedUserDto, result);
        verify(userRepository).save(user);
    }

    @Test
    void createNewUserFailedThenThrowUserValidationException() {
        UserDto userDto = UserMapper.toUserDto(user);
        when(userRepository.save(user)).thenThrow(new UserValidationException("Email must not be null"));

        assertThrows(UserValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void getAllUsersSuccessfulThenReturnListOfUser() {
        List<UserDto> userDtos = UserMapper.mapToUserDto(List.of(user));

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(userDtos, result);
        verify(userRepository).findAll();
    }

    @Test
    void shouldUpdateUserSuccessfullyThenReturnUserDto() {
        UserDto userUpdate = UserDto.builder()
                .id(1L)
                .name("name")
                .email("testEmail@ya.ru")
                .build();
        User userUpdated = new User(1L, "name", "testEmail@ya.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(userUpdated)).thenReturn(userUpdated);

        UserDto result = userService.update(userUpdate, 1L);

        assertEquals(userUpdate, result);
        verify(userRepository).save(UserMapper.toUser(userUpdate));
    }

    @Test
    void shouldNotUpdateUserThenThrowEmailConflictException() {
        UserDto userUpdate = UserDto.builder()
                .id(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(EmailConflictException.class, () -> userService.update(userUpdate, 1L));
        verify(userRepository, never()).save(UserMapper.toUser(userUpdate));
    }
}
