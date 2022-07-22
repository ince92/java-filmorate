package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmServiceTest {
    private final FilmService filmService;
    private final DirectorService directorService;

    @Test
    void testFindFilmsByTitle() {
        var director = new Director(1, "person");
        var directors = Set.of(director);

        directorService.create(director);

        var mpa = new MPA("G", 1);
        var date = LocalDate.of(1990, 1, 1);

        var f1 = new Film(1L, "Film1", "Action", date, 7200L, mpa, Set.of(), directors);
        var f2 = new Film(2L, "Film2", "Action", date, 7200L, mpa, Set.of(), directors);

        filmService.create(f1);
        filmService.create(f2);

        var films = filmService.findFilms("Film", Set.of("title"));

        assertEquals(2, films.size());

        assertIterableEquals(List.of(f1, f2), films);

        assertEquals(0, filmService.findFilms("nobody", Set.of("title")).size());
    }

    @Test
    void testFindFilmsByDirector() {
        var director = new Director(1, "person");
        var directors = Set.of(director);

        directorService.create(director);

        var mpa = new MPA("G", 1);
        var date = LocalDate.of(1990, 1, 1);

        var f1 = new Film(1L, "Film1", "Action", date, 7200L, mpa, Set.of(), directors);
        var f2 = new Film(2L, "Film2", "Action", date, 7200L, mpa, Set.of(), directors);

        filmService.create(f1);
        filmService.create(f2);

        var films = filmService.findFilms("person", Set.of("director"));

        assertEquals(2, films.size());

        assertIterableEquals(List.of(f1, f2), films);

        assertEquals(0, filmService.findFilms("nobody", Set.of("director")).size());
    }
}
