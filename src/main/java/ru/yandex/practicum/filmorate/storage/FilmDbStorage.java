package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.storageInterface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.storageInterface.GenreStorage;
import ru.yandex.practicum.filmorate.storage.storageInterface.MpaStorage;

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
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage){
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
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
                    genreStorage.addGenreToFilm(film.getId(), genre.getId());
                }
                film.setGenres(new HashSet<>(genreStorage.getGenreSetByFilm(film.getId())));//заполним из бд
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
        genreStorage.deleteGenresByFilm(film.getId());
        if (film.getGenres() != null) {

            for (Genre genre : film.getGenres()) {
                genreStorage.addGenreToFilm(film.getId(), genre.getId());
            }

            film.setGenres(new HashSet<>(genreStorage.getGenreSetByFilm(film.getId())));//заполним из бд
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
        MPA mpa = mpaStorage.findMpaById(rs.getInt("MPA")).get();
        Set<Genre> genres = new HashSet<>(genreStorage.getGenreSetByFilm(id));

        return new Film(id,name,description,releaseDate,duration,mpa,genres);
    }

    public List<Film> findPopular(int count) {
        String sqlQuery = "select F.FILM_ID as FILM_ID,  f.DESCRIPTION as DESCRIPTION" +
                ", f.DURATION as DURATION, f.FILM_NAME as FILM_NAME, f.MPA as MPA, f.RELEASE_DATE as RELEASE_DATE "+
                "from FILMS F  left join LIKES l on F.FILM_ID = l.FILM_ID  group by F.FILM_ID " +
                "order by  COUNT(distinct l.USER_ID) desc limit ?";

        return jdbcTemplate.query(sqlQuery,(rs, rowNum) -> makeFilm(rs),count);


    }

}
