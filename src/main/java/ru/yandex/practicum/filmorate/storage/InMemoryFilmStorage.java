package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private long id;
    private final Map<Long, Film> films = new HashMap<>();

    public Film create(Film film) {
        films.put(++id, film);
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Optional<Film> findFilmById(long id) {
        if (!films.containsKey(id)) {
            return Optional.empty();
        } else {
            return Optional.of(films.get(id));
        }
    }
}
