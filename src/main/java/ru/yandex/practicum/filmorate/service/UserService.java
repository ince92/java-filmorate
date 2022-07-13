package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.storageInterface.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.storageInterface.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
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
        findUserByID(user.getId()); //вместо проверки на существование такого объекта
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

    public User findUserByID(long id) {
        return userStorage.findUserById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
    }

    public boolean addFriend(long userId, long friendId) {
        findUserByID(userId);
        findUserByID(friendId);
        friendshipStorage.addFriend(userId,friendId);
        return true;
    }

    public boolean deleteFriend(long userId, long friendId) {
        findUserByID(userId);
        findUserByID(friendId);
        friendshipStorage.deleteFriend(userId,friendId);
        return true;
    }

    public List<User> findFriends(long userId) {
        findUserByID(userId);
        return friendshipStorage.findFriends(userId);

    }

    public List<User> findCommonFriends(long userId, long otherId) {
        findUserByID(userId);
        findUserByID(otherId);
        return friendshipStorage.findCommonFriends(userId,otherId);
    }

}
