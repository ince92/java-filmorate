package ru.yandex.practicum.filmorate.storage.storageInterface;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Optional<Director> create(Director director);
    Optional<Director> update(Director director);
    List<Director> findAll();
    Optional<Director> findDirectorById(long id);
    void delete(long id);
    void addDirectorToFilm(long filmId, long directorId);
    List<Director> getFilmDirectors(long filmID);
    void deleteDirectorsByFilm(long filmId);
}
