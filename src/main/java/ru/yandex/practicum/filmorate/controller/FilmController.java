package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;

@RestController
@Validated
@Slf4j
public class FilmController {
      private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        List<Film> films = filmService.findAll();
        log.debug("Текущее количество фильмов: {} ", films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        Film newFilm = filmService.create(film);
        log.info("Добавлен фильм - {}", newFilm.getName());
        return newFilm;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        Film updatedFilm = filmService.update(film);
        log.info("Обновлен фильм - {}", updatedFilm.getName());
        return updatedFilm;
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilm(@PathVariable("id") long id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/films/{filmId}")
    public Film findFilmById(@PathVariable("filmId") long filmId) {
        return filmService.findFilmById(filmId);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> findPopular(@Positive @RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.findPopular(count);
    }
}


