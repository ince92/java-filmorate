package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.storageInterface.MpaStorage;

import java.util.List;
@Service
public class MpaService {
   private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MPA> findAll() {
         return mpaStorage.findAll();
    }
    public MPA findMpaById(int genreId) {
        return mpaStorage.findMpaById(genreId).orElseThrow(() -> new NotFoundException("mpa с таким id не найден!"));
    }
}
