package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDBStorage genreDBStorage;
    private final MpaDBStorage mpaDBStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDBStorage genreDBStorage, MpaDBStorage mpaDBStorage){
        this.jdbcTemplate = jdbcTemplate;
        this.genreDBStorage = genreDBStorage;
        this.mpaDBStorage = mpaDBStorage;
    }

    public Film create(Film film) {
        String sqlQuery = "insert into FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE,DURATION,MPA) values (?, ?, ?, ?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

            film.setId(keyHolder.getKey().longValue());
            //установим жанры фильма
            if (film.getGenres()!=null) {
                for (Genre genre : film.getGenres()) {
                    genreDBStorage.addGenreToFilm(film.getId(), genre.getId());
                }
                film.setGenres(new HashSet<>(genreDBStorage.getGenreSetByFilm(film.getId())));//заполним из бд
            }
           return film;
    }

    public Film update(Film film) {

        String sqlQuery = "update FILMS set " +
                "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA = ? " +
                "where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        genreDBStorage.deleteGenresByFilm(film.getId());
        if (film.getGenres() != null) {

            for (Genre genre : film.getGenres()) {
                genreDBStorage.addGenreToFilm(film.getId(), genre.getId());
            }

            film.setGenres(new HashSet<>(genreDBStorage.getGenreSetByFilm(film.getId())));//заполним из бд
        }
        return film;
    }

    public List<Film> findAll() {
        String sql = "select * from FILMS ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    public Optional<Film> findFilmById(long id)  {
        String sql = "select * from FILMS where FILM_ID = ?";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs),id);
        if (films.size()==0){
            return Optional.empty();
        }else{
            return Optional.of(films.get(0));
        }


    }
    public Film makeFilm(ResultSet rs) throws SQLException {

        long id = rs.getLong("FILM_ID");
        String description = rs.getString("DESCRIPTION");
        String name = rs.getString("FILM_NAME");
        Date release = rs.getDate("RELEASE_DATE");
        LocalDate releaseDate;
        if (release == null){
            releaseDate = null;
        }else{
            releaseDate = release.toLocalDate();
        }
        long duration = rs.getLong("DURATION");
        MPA mpa = mpaDBStorage.findMpaById(rs.getInt("MPA")).get();
        Set<Genre> genres = new HashSet<>(genreDBStorage.getGenreSetByFilm(id));

        return new Film(id,name,description,releaseDate,duration,mpa,genres);
    }
}
