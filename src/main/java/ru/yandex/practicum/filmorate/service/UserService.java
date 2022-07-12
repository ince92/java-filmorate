package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipDBStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final UserStorage userStorage;
    private final FriendshipDBStorage friendshipDBStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendshipDBStorage friendshipDBStorage) {
        this.userStorage = userStorage;
        this.friendshipDBStorage = friendshipDBStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);
        findUser(user.getId()); //вместо проверки на существование такого объекта
        return userStorage.update(user);
    }

    private void validateUser(User user) {
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

    public User findUser(long id) {
        return userStorage.findUserById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
    }

    public boolean addFriend(long userId, long friendId) {
        findUser(userId);
        findUser(friendId);
        friendshipDBStorage.addFriend(userId,friendId);
        return true;
    }

    public boolean deleteFriend(long userId, long friendId) {
        findUser(userId);
        findUser(friendId);
        friendshipDBStorage.deleteFriend(userId,friendId);
        return true;
    }

    public List<User> findFriends(long userId) {
        findUser(userId);
        return friendshipDBStorage.findFriends(userId);

    }

    public List<User> findCommonFriends(long userId, long otherId) {
        findUser(userId);
        findUser(otherId);
        return friendshipDBStorage.findCommonFriends(userId,otherId);
    }

}
