package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.storageInterface.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director create(Director director) {
        return directorStorage.create(director).get();
    }

    public Director update(Director director) {
        findDirectorById(director.getId());
        return directorStorage.update(director).get();
    }

    public Director findDirectorById(long id) {
        return directorStorage.findDirectorById(id).orElseThrow(() ->
                new NotFoundException("Режиссёр с id" + id +" не найден!"));
    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public void delete(long id) {
        directorStorage.delete(id);
    }
}
