package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public List<Genre> findAll() {
        List<Genre> genres = genreService.findAll();
        log.debug("Текущее количество жанров: {} ", genres.size());
        return genres;
    }


    @GetMapping("/genres/{genreId}")
    public Genre findGenreById(@PathVariable("genreId") int genreId) {
        return genreService.findGenreById(genreId);
    }


}
