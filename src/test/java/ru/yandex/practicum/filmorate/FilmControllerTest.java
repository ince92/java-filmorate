package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class FilmControllerTest {
    public FilmController filmController;


    @BeforeEach
    public void prepare() {
        //filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),new UserService(new InMemoryUserStorage(), new FriendshipDBStorage(new JdbcTemplate(),new UserDbStorage(new JdbcTemplate()))), new LikesDBStorage(new JdbcTemplate(),new FilmDbStorage(new JdbcTemplate(),new GenreDBStorage()))));
    }

    @Test
    void createFilmTest() {
        Film film = new Film(1, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L,null,new HashSet<>());
        Film film1 = filmController.create(film);
        assertEquals(film, film1, "Фильмы не создаются корректно");
    }

    @Test
    void createEmptyNameFilmTest() {
        Film film = new Film(1, "", "Action", LocalDate.of(1990, 1, 1), 7200L,null,new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.create(film), "Создан фильм " +
                "с пустым названием");
    }

    @Test
    void createLongDescriptionFilmTest() {
        Film film = new Film(1, "", "Actionlksjdnfvkiuhfdgkdjblkmnfgkjbnkfgblkjfdgnbkijndfgbkinfg" +
                "kibnkfgbn" +
                "dflvkjmldfmvkldmnfvlkmdfvlkmdflvvm" +
                "dflkvmklkdmfvolkmdflvkmdflvkmdlfkfvmldfkfv" +
                "dlfkvmldfmvlkdmffvldmfvlvkkdmffvvldmfvlmdfvv" +
                "dlfkvmdlkfmvlkdfmvlkdmfvlkdmfvlkdmffvlkmdfflvkvmdf" +
                "dflkmvlkdfmvlkdmfvlkdmfvlmdfflvkmdfflkvmdflkvvmdlkfvm" +
                "dlfkvmldfmvlkdmfvlkmdfvlkvmdfvlkkmdflkvvmdlkfvmvdlkfmvvdlfmvldkfmv" +
                "", LocalDate.of(1990, 1, 1), 7200L,null,new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.create(film), "Создан фильм " +
                "с очень длинным описанием");
    }

    @Test
    void createIncorrectDateFilmTest() {
        Film film = new Film(1, "", "Action", LocalDate.of(1985, 12, 27),
                7200L,null, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.create(film), "Создан фильм " +
                "с некорректной датой выпуска");
    }

    @Test
    void createNegativeDurationFilmTest() {
        Film film = new Film(1, "", "Action", LocalDate.of(1985, 12, 28), 7200L,null,new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.create(film), "Создан фильм " +
                "с отрицательной продолжительностью");
    }
}
