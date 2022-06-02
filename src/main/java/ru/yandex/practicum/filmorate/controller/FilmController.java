package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {

    private int id;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {} ", films.size());
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        log.info("Добавлен фильм - {}", film.getName());
        films.put(++id, film);
        film.setId(id);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.info("Фильм с id = {} не найден!", film.getId());
            throw new ValidationException("Фильм с таким id не найден! Обновление невозможно!");
        }
        log.info("Обновлен фильм - {}", film.getName());
        films.put(film.getId(), film);
        return film;
    }

    public void validateFilm(Film film) {
        if (film == null) {
            log.info("Фильм = null");
            throw new ValidationException("Фильм = null");
        }
        if (film.getName().isBlank()) {
            log.info("Незвание фильма не заполнено!");
            throw new ValidationException("Незвание фильма не заполнено!");
        }
        if (film.getDescription().length() > 200) {
            log.info("Длина описания фильма больше 200 символов !");
            throw new ValidationException("Длина описания фильма больше 200 символов !");
        }
        if (film.getDuration() < 0) {
            log.info("У фильма отрицательная длительность: {}", film.getDuration());
            throw new ValidationException("У фильма отрицательная длительность!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Даты релиза фильма некорректна: {}", film.getReleaseDate());
            throw new ValidationException("Даты релиза фильма некорректна!");
        }

    }
}
