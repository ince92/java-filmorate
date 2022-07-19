package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.storageInterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.storageInterface.LikesStorage;
import ru.yandex.practicum.filmorate.storage.storageInterface.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
     @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikesStorage likesStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
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
        findFilmById(film.getId()); //вместо проверки на существование такого объекта
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

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с таким id не найден!"));
    }

    public Film addLike(long id, long userId) {
        checkUser(userId);
        likesStorage.addLike(id,userId);
        return findFilmById(id);
    }

    public Film deleteLike(long id, long userId) {
        checkUser(userId);
        likesStorage.deleteLike(id,userId);
        return findFilmById(id);
    }

    public List<Film> findPopular(int count) {
        return filmStorage.findPopular(count);
    }

    private void checkUser(long userId){
        userStorage.findUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));

    }

    public void deleteFilm(long id) {
        var removed = filmStorage.remove(id);
        if (!removed) {
            throw new NotFoundException("Фильм не найден");
        }
    }
}
