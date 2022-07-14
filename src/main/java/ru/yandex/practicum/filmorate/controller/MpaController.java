package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Validated
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public List<MPA> findAll() {
        List<MPA> mpas = mpaService.findAll();
        log.debug("Текущее количество mpa: {} ", mpas.size());
        return mpas;
    }


    @GetMapping("/mpa/{mpaId}")
    public MPA findMpaById(@PathVariable("mpaId") int mpaId) {
        return mpaService.findMpaById(mpaId);
    }


}
