package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsersResponse() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto userCreateResponse(@RequestBody @Valid UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping(value = "/{userId}")
    public UserDto userUpdateResponse(@RequestBody @Valid UserDto userDto, @PathVariable Long userId) {
        return userService.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto findUserByIdResponse(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void userDeleteResponse(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
