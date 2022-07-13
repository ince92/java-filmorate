package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.storageInterface.GenreStorage;

import java.util.List;
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreDBStorage genreDBStorage) {
        this.genreStorage = genreDBStorage;
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }
    public Genre findGenreById(int genreId) {
        return genreStorage.findGenreById(genreId).orElseThrow(() ->
                new NotFoundException("Жанр с таким id не найден!"));
    }
}
