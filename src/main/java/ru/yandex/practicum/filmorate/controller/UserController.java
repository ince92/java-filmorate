package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    // создаём логер
    private int id;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public ArrayList<User> findAll() {
        log.info("Количество пользователей - {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        validateUser(user);
        log.info("Добавлен пользователь - {}", user.getName());
        users.put(++id, user);
        user.setId(id);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь с id = {} не найден!", user.getId());
            throw new ValidationException("Пользователь с таким id не найден! Обновление невозможно!");
        }
        log.info("Обновлен пользователь- {}", user.getName());
        users.put(user.getId(), user);
        return user;
    }

    public void validateUser(User user) {
        if (user == null) {
            log.info("Пользователь = null");
            throw new ValidationException("Пользователь = null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Некорректный имеил -{}", user.getEmail());
            throw new ValidationException("Некорректный имеил");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("Некорректный логин -{}", user.getLogin());
            throw new ValidationException("Некорректный логин");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователю установлен логин - {}", user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Некорректная дата рождения -{}", user.getBirthday());
            throw new ValidationException("дата рождения некорректна");
        }

    }
}
