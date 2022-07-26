package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.storageInterface.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDBStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public void addGenreToFilm(long filmId, int genreId) {
        String sqlQuery = "merge into FILM_GENRES (FILM_ID, GENRE_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    public List<Genre> getGenreSetByFilm(long filmId) {
        String sqlQuery = "select g.GENRE_ID,g.GENRE_NAME from FILM_GENRES as f left join GENRES as g on " +
                "f.GENRE_ID = g.GENRE_ID where f.FILM_ID = ? order by g.GENRE_ID";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    public void deleteGenresByFilm(long filmId) {
        String sqlQuery = "delete from FILM_GENRES where FILM_ID= ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    public Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("GENRE_ID");
        String name = rs.getString("GENRE_NAME");
        return new Genre(name, id);
    }

    public List<Genre> findAll() {
        String sql = "select * from GENRES ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    public Optional<Genre> findGenreById(int id) {
        String sql = "select * from GENRES where GENRE_ID = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
        if (genres.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(genres.get(0));
        }
    }
}
