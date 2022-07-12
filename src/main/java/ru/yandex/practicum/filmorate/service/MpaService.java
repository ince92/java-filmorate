package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MpaDBStorage;

import java.util.List;
@Service
public class MpaService {
   private final MpaDBStorage mpaDBStorage;

    public MpaService(MpaDBStorage mpaDBStorage) {
        this.mpaDBStorage = mpaDBStorage;
    }

    public List<MPA> findAll() {
         return mpaDBStorage.findAll();
    }
    public MPA findMpa(int genreId) {
        return mpaDBStorage.findMpaById(genreId).orElseThrow(() -> new NotFoundException("mpa с таким id не найден!"));
    }
}
