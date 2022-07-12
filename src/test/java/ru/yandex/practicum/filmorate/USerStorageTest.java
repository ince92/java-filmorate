package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class USerStorageTest {
    private final UserDbStorage userStorage;
    private final UserService userService;
    @Test
    public void testFindUserById() {

        User user = new User(1, "emai@l", "login", "name", LocalDate.of(1992, 9,
                4));
        User user1 = userStorage.create(user);



        Optional<User> UserOptional = userStorage.findUserById(1);

        assertThat(UserOptional)
                .isPresent()
                .hasValueSatisfying(userTest ->
                        assertThat(userTest).hasFieldOrPropertyWithValue("id", 1L)
                );
    }
    @Test
    public void testFindUserByIncorrectId() {

        Optional<User> userOptional = userStorage.findUserById(1);
        assertTrue(userOptional.isEmpty(),"Найден пользователь по некорректному айди");
    }
    @Test
    public void testCreateUser() {
        User user = new User(1, "emai@l", "login", "name", LocalDate.of(1992, 9,
                4));
        User user1 = userStorage.create(user);


        Optional<User> userOptional = userStorage.findUserById(1);
        assertTrue(userOptional.get().getName()=="name" && userOptional.get().getId()==1L,"Пользователь " +
                "добавляется некорректно");
    }

    @Test
    public void testUpdateUser() {
        User user = new User(1, "emai@l", "login", "name", LocalDate.of(1992, 9,
                4));
        userStorage.create(user);
        user.setName("name updated");
        User user1 = userStorage.update(user);


        Optional<User> userOptional = userStorage.findUserById(user1.getId());
        assertTrue(userOptional.get().getName()=="name updated" && userOptional.get().getId()==1L,"Пользователь " +
                "обновляется некорректно");
    }
    @Test
    public void testIncorrectIdUpdateUser() {
        User user = new User(1, "emai@l", "login", "name", LocalDate.of(1992, 9,
                4));
        userStorage.create(user);

        User user1 = new User(2, "emai@l1", "login1", "name1", LocalDate.of(1992, 9,
                4));


        assertThrows(NotFoundException.class,  () -> userService.update(user1), "Пользователь обновляется некорректно");

    }
    @Test
    public void testFindAllUser() {
        User user = new User(1, "emai@l", "login", "name", LocalDate.of(1992, 9,
                4));
        userStorage.create(user);

        User user1 = new User(2, "emai@l1", "login1", "name1", LocalDate.of(1992, 9,
                4));
        userStorage.create(user1);

        assertTrue(userService.findAll().size()==2, "Метод возвращает неправильный список");

    }
    @Test
    public void testFindAllEmptyUser() {

        assertTrue(userService.findAll().size()==0, "Метод возвращает неправильный список");

    }
}
