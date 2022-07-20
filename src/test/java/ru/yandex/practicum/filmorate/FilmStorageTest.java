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
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmStorageTest {
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;
    @Test
    public void testFindFilmById() {

        Film film = new Film(1L, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);
        Film film1 = filmStorage.create(film);



        Optional<Film> filmOptional = filmStorage.findFilmById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(filmTest ->
                        assertThat(filmTest).hasFieldOrPropertyWithValue("id", 1L)
                );
    }
    @Test
    public void testFindFilmByIncorrectId() {

        Optional<Film> filmOptional = filmStorage.findFilmById(1);
        assertTrue(filmOptional.isEmpty(),"Найден фильм по некорректному айди");
       }
    @Test
    public void testCreateFilm() {
        Film film = new Film(1L, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);
        Film film1 = filmStorage.create(film);


        Optional<Film> filmOptional = filmStorage.findFilmById(1);
        assertTrue(filmOptional.get().getName()=="Film" && filmOptional.get().getId()==1L,"Фильм " +
                "добавляется некорректно");
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film(1L, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);
        filmStorage.create(film);
        film.setName("Film updated");
        Film film1 = filmStorage.update(film);


        Optional<Film> filmOptional = filmStorage.findFilmById(film1.getId());
        assertTrue(filmOptional.get().getName()=="Film updated" && filmOptional.get().getId()==1L,"Фильм " +
                "обновляется некорректно");
    }
    @Test
    public void testIncorrectIdUpdateFilm() {
        Film film = new Film(1L, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);
        filmStorage.create(film);
        Film film1 = new Film(2L, "Film2", "Action2", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);


        assertThrows(NotFoundException.class,  () -> filmService.update(film1), "Фильм обновляется некорректно");

    }
    @Test
    public void testFindAllFilm() {
        Film film = new Film(1L, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);
        filmStorage.create(film);
        Film film1 = new Film(2L, "Film2", "Action2", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);
        filmStorage.create(film1);

        assertTrue(filmService.findAll().size()==2, "Метод возвращает неправильный список");

    }
    @Test
    public void testFindAllEmptyFilm() {

        assertTrue(filmService.findAll().size()==0, "Метод возвращает неправильный список");

    }

    @Test
    void testFilmDeletion() {
        Film film2 = new Film(1L, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);

        Film film1 = new Film(2L, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null);


        filmStorage.create(film1);
        filmStorage.create(film2);

        assertEquals(2, filmStorage.findAll().size());

        filmStorage.remove(1L);

        var films = filmStorage.findAll();
        assertEquals(1, films.size());

        assertEquals(2L, films.get(0).getId());
    }
}
