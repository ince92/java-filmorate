package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.storageInterface.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Director> create(Director director) {
        String sqlQuery = "insert into DIRECTORS (DIRECTOR_NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        director.setId(keyHolder.getKey().longValue());
        return Optional.of(director);
    }

    @Override
    public Optional<Director> update(Director director) {
        String sqlQuery = "update DIRECTORS set DIRECTOR_NAME = ? where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return Optional.of(director);
    }

    @Override
    public List<Director> findAll() {
        String sqlQuery = "select * from DIRECTORS";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> findDirectorById(long id) {
        String sqlQuery = "select * from DIRECTORS where DIRECTOR_ID = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs), id);
        if (directors.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(directors.get(0));
        }
    }

    @Override
    public void delete(long id) {
        String sqlQuery = "delete from DIRECTORS where DIRECTOR_ID = ?";
        if (jdbcTemplate.update(sqlQuery, id) == 0) {
            throw new RuntimeException("Не удалось удалить режиссёра с id " + id);
        }
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return new Director(rs.getLong("DIRECTOR_ID"), rs.getString("DIRECTOR_NAME"));
    }

    @Override
    public void addDirectorToFilm(long filmId, long directorId) {
        String sqlQuery = "merge into FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery,filmId,directorId);
    }

    @Override
    public List<Director> getFilmDirectors(long filmID) {
        String sqlQuery = "select D.DIRECTOR_ID, D.DIRECTOR_NAME from FILM_DIRECTORS as FD " +
                "left join DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "WHERE FILM_ID = ? " + "ORDER BY D.DIRECTOR_ID ";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findDirectorById(
                rs.getInt("DIRECTOR_ID")).get(), filmID);
    }

    public void deleteDirectorsByFilm(long filmId) {
        String sqlQuery = "delete from FILM_DIRECTORS where FILM_ID= ?";
        jdbcTemplate.update(sqlQuery,filmId);

    }
}
