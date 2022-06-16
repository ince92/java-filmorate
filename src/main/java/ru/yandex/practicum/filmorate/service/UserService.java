package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public ArrayList<User> findAll() {
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
        User user = findUser(userId);
        User friend = findUser(friendId);
        Set<Long> friends1 = user.getFriends();
        friends1.add(friendId);
        Set<Long> friends2 = friend.getFriends();
        friends2.add(userId);
        return true;
    }

    public boolean deleteFriend(long userId, long friendId) {
        User user = findUser(userId);
        User friend = findUser(friendId);
        Set<Long> friends1 = user.getFriends();
        friends1.remove(friendId);
        Set<Long> friends2 = friend.getFriends();
        friends2.remove(userId);
        return true;
    }

    public List<User> findFriends(long userId) {
        User user = findUser(userId);
        Set<Long> friends = user.getFriends();
        return friends.stream()
                .map(f0 -> findUser(f0))
                .collect(Collectors.toList());

    }

    public List<User> findCommonFriends(long userId, long otherId) {
        User user = findUser(userId);
        Set<Long> friends = user.getFriends();
        User otherUser = findUser(otherId);
        Set<Long> otherFriends = otherUser.getFriends();
        Set<Long> intersection = new HashSet<>(friends);
        intersection.retainAll(otherFriends);
        return intersection.stream()
                .map(f0 -> findUser(f0))
                .collect(Collectors.toList());
    }
}
