package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long id;

    private final Map<Long, User> users = new HashMap<>();

    public ArrayList<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        users.put(++id, user);
        user.setId(id);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findUserById(long id) {
        if (!users.containsKey(id)) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(id));
        }
    }
}
