package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.userService = userService;
        this.filmStorage = filmStorage;
    }

    public ArrayList<Film> findAll() {
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
        Film film = findFilm(id);
        User user = userService.findUser(userId);
        Set<User> likes = film.getLikes();
        likes.add(user);
        return film;
    }

    public Film deleteLike(long id, long userId) {
        Film film = findFilm(id);
        User user = userService.findUser(userId);
        Set<User> likes = film.getLikes();
        likes.remove(user);
        return film;
    }

    public List<Film> findPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size() * -1))
                .limit(count)
                .collect(Collectors.toList());
    }

}
