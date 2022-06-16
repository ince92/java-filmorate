package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Optional;

public interface UserStorage {
    ArrayList<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> findUserById(long id);

}
