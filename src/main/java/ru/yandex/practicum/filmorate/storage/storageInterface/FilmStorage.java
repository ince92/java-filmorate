package ru.yandex.practicum.filmorate.storage.storageInterface;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);
    Film update(Film film);
    List<Film> findAll();
    Optional<Film> findFilmById(long id);
    List<Film> findPopular(int count);
    List<Film> findDirectorsFilms(long directorId, String sortBy);
}
