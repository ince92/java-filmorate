package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping()
    public List<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        return directorService.findDirectorById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        Director newDirector = directorService.create(director);
        log.info("Добавлен режиссёр {} с id {}", director.getName(), director.getId());
        return newDirector;
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        Director newDirector = directorService.update(director);
        log.info("Обновлен режиссёр {} с id {}", director.getName(), director.getId());
        return newDirector;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        directorService.delete(id);
    }
}
