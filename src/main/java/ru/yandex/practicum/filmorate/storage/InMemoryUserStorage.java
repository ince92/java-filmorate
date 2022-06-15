package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;


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
