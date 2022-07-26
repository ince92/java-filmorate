package ru.yandex.practicum.filmorate.storage.storageInterface;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film create(Film film);
    Film update(Film film);
    boolean remove(long id);
    List<Film> findAll();
    Optional<Film> findFilmById(long id);
    List<Film> findPopular(int count);
    List<Film> findCommonFilms(long userId, long friendId);
    List<Film> getMostPopularFilms(long count, long genreId, long year);
    List<Film> findDirectorsFilms(long directorId, String sortBy);
    List<Film> findRecommendations(long userId);
    List<Film> findFilms(String query, Set<String> searchKeys);
}
