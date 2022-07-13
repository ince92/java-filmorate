package ru.yandex.practicum.filmorate.storage.storageInterface;

import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    Optional<MPA> findMpaById(long id);
    MPA makeMPA(ResultSet rs) throws SQLException;
    List<MPA> findAll();
}
