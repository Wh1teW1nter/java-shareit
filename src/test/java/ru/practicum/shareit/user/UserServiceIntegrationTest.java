package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    void getAllUsersWhenDbIsEmptyThenReturnEmptyList() {

        List<UserDto> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsersWhenDbHasTwoUsersThenReturnList() {
        User user1 = new User(0L, "user1", "one@ru");
        User user2 = new User(0L, "user2", "two@ru");
        userRepository.save(user1);
        userRepository.save(user2);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }

    @Test
    void createUserWhenUserHasSameEmailThenConflictException() {
        User user = new User(0L, "user", "test@ru");
        userRepository.save(user);
        UserDto userDto = UserDto.builder()
                .name("user")
                .email("test@ru")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userService.create(userDto));
    }

    @Test
    void createUserThenReturnUserDto() {
        UserDto userDto = UserDto.builder()
                .name("user")
                .email("test@ru")
                .build();

        UserDto result = userService.create(userDto);

        assertEquals(1L, result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void createUserWhenUserEmailNullThenUserValidationException() {
        UserDto userDto = UserDto.builder()
                .name("user")
                .email(null)
                .build();

        assertThrows(UserValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void updateUserSuccessfullyThenReturnUpdatedUserDto() {
        User userOld = new User(0L, "userOld", "old@ru");
        userRepository.save(userOld);
        UserDto userUpdate = UserDto.builder()
                .name("userUpdate")
                .email("userUpdate@ru")
                .build();

        UserDto result = userService.update(userUpdate, 1L);

        assertEquals(1L, result.getId());
        assertEquals(userUpdate.getName(), result.getName());
        assertEquals(userUpdate.getEmail(), result.getEmail());
    }

    @Test
    void deleteUserThenBdIsEmpty() {
        User user = new User(0L, "userOld", "old@ru");

        userRepository.save(user);

        assertEquals(1, userRepository.findAll().size());

        userService.delete(1L);

        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void getUserByIdThenReturnCorrectUserDto() {
        User userOld = new User(0L, "userOld", "old@ru");
        User user = new User(0L, "user", "user@ru");

        userRepository.save(userOld);
        userRepository.save(user);

        UserDto userDto1 = userService.findUserById(1L);
        UserDto userDto2 = userService.findUserById(2L);

        assertEquals(1L, userDto1.getId());
        assertEquals(userOld.getName(), userDto1.getName());
        assertEquals(userOld.getEmail(), userDto1.getEmail());
        assertEquals(2L, userDto2.getId());
        assertEquals(user.getName(), userDto2.getName());
        assertEquals(user.getEmail(), userDto2.getEmail());
    }
}
