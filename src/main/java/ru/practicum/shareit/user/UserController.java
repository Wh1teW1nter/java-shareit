package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsersDtoResponse() {
        log.info("пришел GET запрос /users ");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto userDtoCreateResponse(@RequestBody @Valid UserDto userDto) {
        log.info("пришел POST запрос /users с телом: {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping(value = "/{userId}")
    public UserDto userDtoUpdateResponse(@RequestBody @Valid UserDto userDto, @PathVariable Long userId) {
        log.info("пришел PATCH запрос /users/{userId} с телом: {} и userId: {}", userDto, userId);
        return userService.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto findUserByIdResponse(@PathVariable Long userId) {
        log.info("пришел GET запрос /users/{userId} с userId: {}", userId);
        return userService.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void userDtoDeleteResponse(@PathVariable Long userId) {
        log.info("пришел DELETE запрос /users/{userId} с userId: {}", userId);
        userService.delete(userId);
    }
}
