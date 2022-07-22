package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;

@RestController
@Validated
@Slf4j
public class FilmController {
    private final FilmService filmService;
      private final DirectorService directorService;

    @Autowired
    public FilmController(FilmService filmService, DirectorService directorService) {
        this.filmService = filmService;
        this.directorService = directorService;
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
    public List<Film> getMostPopularFilms(@Positive
              @RequestParam(name = "count", defaultValue = "10", required = false) long count
            , @RequestParam(name = "genreId", defaultValue = "0", required = false) long genreId
            , @RequestParam(name = "year", defaultValue = "0", required = false) long year) {
        return filmService.getMostPopularFilms(count, genreId, year);
    }

    @GetMapping("/films/common")
    public List<Film> findCommonFilms(@Positive @RequestParam(name = "userId") long userId
            , @RequestParam(name = "friendId") long friendId) {
        return filmService.findCommonFilms(userId, friendId);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getDirectorsFilms(@PathVariable("directorId") long directorId,
                                        @RequestParam(value = "sortBy", required = false) String sortBy) {
        directorService.findDirectorById(directorId);
        return filmService.findDirectorsFilms(directorId, sortBy);
    }

    @GetMapping("/films/search")
    public List<Film> searchFilms(@RequestParam("query") String query, @RequestParam("by") String searchBy) {
        var searchKeys = Set.of(searchBy.split(","));
        return filmService.findFilms(query, searchKeys);
    }
}


