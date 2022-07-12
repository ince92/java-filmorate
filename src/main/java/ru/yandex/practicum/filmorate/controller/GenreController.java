package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Validated
public class GenreController {
        private static final Logger log = LoggerFactory.getLogger(ru.yandex.practicum.filmorate.controller.FilmController.class);
        private final GenreService genreService;

        @Autowired
        public GenreController(GenreService genreService) {
            this.genreService = genreService;
        }

        @GetMapping("/genres")
        public List<Genre> findAll() {
            List<Genre> genres = genreService.findAll();
            log.debug("Текущее количество жанров: {} ", genres.size());
            return genres;
        }


        @GetMapping("/genres/{genreId}")
        public Genre findGenre(@PathVariable("genreId") int genreId) {
            return genreService.findGenre(genreId);
        }


}
