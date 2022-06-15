package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    // создаём логер
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ArrayList<User> findAll() {
        ArrayList<User> users = userService.findAll();
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
    public User findUser(@PathVariable("userId") Integer userId) {
        return userService.findUser(userId);
    }

    @PutMapping(value = "/users/{userId}/friends/{friendId}")
    public boolean addFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping(value = "/users/{userId}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable("userId") Integer userId, @PathVariable("friendId") Integer friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/users/{userId}/friends")
    public List<User> findFriends(@PathVariable("userId") Integer userId) {
        return userService.findFriends(userId);
    }

    //GET /users/{id}/friends/common/{otherId}
    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("userId") Integer userId,
                                        @PathVariable("otherId") Integer otherId) {
        return userService.findCommonFriends(userId, otherId);
    }
}
