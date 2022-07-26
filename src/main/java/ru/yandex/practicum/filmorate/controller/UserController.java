package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EventService eventService;

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

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
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

    //GET /users/{id}/recommendations
    @GetMapping("/users/{userId}/recommendations")
    public List<Film> findRecommendations(@PathVariable("userId") long userId) {
        return userService.findRecommendations(userId);
    }


    @GetMapping("/users/{userId}/feed")
    public List<Event> showUserHistory(@PathVariable("userId") Integer userId) {
        log.debug("Запрошена история событий пользователя с ID: " + userId);
        return eventService.showUserHistory(userId);
    }
}
