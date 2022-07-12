package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesDBStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikesDBStorage likesDBStorage;
     @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService, LikesDBStorage likesDBStorage) {
        this.userService = userService;
        this.filmStorage = filmStorage;
        this.likesDBStorage = likesDBStorage;
     }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        findFilm(film.getId()); //вместо проверки на существование такого объекта
        return filmStorage.update(film);
    }

    private void validateFilm(Film film) {
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

    public Film findFilm(long id) {
        return filmStorage.findFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с таким id не найден!"));
    }

    public Film addLike(long id, long userId) {
        userService.findUser(userId);
        likesDBStorage.addLike(id,userId);
        return findFilm(id);
    }

    public Film deleteLike(long id, long userId) {
        userService.findUser(userId);
        likesDBStorage.deleteLike(id,userId);
        return findFilm(id);
    }

    public List<Film> findPopular(int count) {
        return likesDBStorage.findPopular(count);
    }

}
