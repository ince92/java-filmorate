package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        List<User> users = userService.findAll();
        log.info("Количество пользователей - {}", users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        User newUser = userService.create(user);
        log.info("Добавлен пользователь - {}", newUser.getName());
        return newUser;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        User updatedUser = userService.update(user);
        log.info("Обновлен пользователь- {}", updatedUser.getName());
        return updatedUser;
    }

    @GetMapping("/users/{userId}")
    public User findUserByID(@PathVariable("userId") Integer userId) {
        return userService.findUserByID(userId);
    }

    @PutMapping(value = "/users/{userId}/friends/{friendId}")
    public boolean addFriend(@PathVariable("userId") long userId, @PathVariable("friendId") long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping(value = "/users/{userId}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable("userId") long userId, @PathVariable("friendId") long friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/users/{userId}/friends")
    public List<User> findFriends(@PathVariable("userId") long userId) {
        return userService.findFriends(userId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("userId") long userId,
                                        @PathVariable("otherId") long otherId) {
        return userService.findCommonFriends(userId, otherId);
    }
}
