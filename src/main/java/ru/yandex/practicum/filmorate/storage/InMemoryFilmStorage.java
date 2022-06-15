package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id;
    private final Map<Integer, Film> films = new HashMap<>();

    public Film create(Film film) {
        films.put(++id, film);
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с таким id не найден! Обновление невозможно!");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film findFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с таким id не найден!");
        } else {
            return films.get(id);
        }
    }
}
