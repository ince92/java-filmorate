package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class UserControllerTest {
    public UserController userController;

    @BeforeEach
    public void prepare() {
       // userController = new UserController(new UserService(new InMemoryUserStorage(), new FriendshipDBStorage()));
    }

    @Test
    void createUserTest() {
        User user = new User(1, "emai@l", "login", "name", LocalDate.of(1992, 9,
                4));
        User user1 = userController.create(user);
        assertEquals(user, user1, "Пользователи не создаются корректно");
    }

    @Test
    void createInvalidEmailUserTest() {
        User user = new User(1, "email", "login", "name", LocalDate.of(1992, 9,
                4));
        assertThrows(ValidationException.class, () -> userController.create(user), "Создан пользователь " +
                "с неправильным имеилом");
    }

    @Test
    void createEmptyEmailUserTest() {
        User user = new User(1, "", "login", "name", LocalDate.of(1992, 9,
                4));
        assertThrows(ValidationException.class, () -> userController.create(user), "Создан пользователь " +
                "с неправильным имеилом");
    }

    @Test
    void createEmptyLoginUserTest() {
        User user = new User(1, "emai@l", "", "name", LocalDate.of(1992, 9,
                4));
        assertThrows(ValidationException.class, () -> userController.create(user), "Создан пользователь " +
                "с неправильным логином");
    }

    @Test
    void createLoginWithSpaceUserTest() {
        User user = new User(1, "emai@l", "lo gin", "name", LocalDate.of(1992, 9,
                4));
        assertThrows(ValidationException.class, () -> userController.create(user), "Создан пользователь " +
                "с неправильным логином");
    }

    @Test
    void createEmptyNameUserTest() {
        User user = new User(1, "emai@l", "login", "", LocalDate.of(1992, 9,
                4));
        User user1 = userController.create(user);
        assertEquals(user1.getName(), "login", "Создан пользователь " +
                "c неверным именем");
    }

    @Test
    void createFutureBirthdayUserTest() {
        User user = new User(1, "emai@l", "login", "", LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.create(user), "Создан пользователь " +
                "с днем рождения в будущем");
    }

}
