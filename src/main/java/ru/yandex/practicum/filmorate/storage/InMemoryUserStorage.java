package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int id;

    private final Map<Integer, User> users = new HashMap<>();

    public ArrayList<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        users.put(++id, user);
        user.setId(id);
        return user;
    }

    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с таким id не найден! Обновление невозможно!");
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findUserById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с таким id не найден!");
        } else {
            return users.get(id);
        }
    }
}
