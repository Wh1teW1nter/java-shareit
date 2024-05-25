package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User create(User user);

    User update(User oldUser, User newUser, Long id);

    User findUserById(Long id);

    void delete(Long id);

    boolean doesIdExist(Long id);

    boolean doesEmailNotExist(String email);
}
