package ru.yandex.practicum.filmorate.storage.storageInterface;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    void addGenreToFilm(long filmId, int genreId);
    List<Genre> getGenreSetByFilm(long filmId);
    void deleteGenresByFilm(long filmId);
    Genre makeGenre(ResultSet rs) throws SQLException;
    List<Genre> findAll();
    Optional<Genre> findGenreById(int id);
}
