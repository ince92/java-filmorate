package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDBStorage;

import java.util.List;
@Service
public class GenreService {
    private final GenreDBStorage genreDBStorage;

    public GenreService(GenreDBStorage genreDBStorage) {
        this.genreDBStorage = genreDBStorage;
    }

    public List<Genre> findAll() {
        return genreDBStorage.findAll();
    }
    public Genre findGenre(int genreId) {
        return genreDBStorage.findGenreById(genreId).orElseThrow(() ->
                new NotFoundException("Жанр с таким id не найден!"));
    }
}
