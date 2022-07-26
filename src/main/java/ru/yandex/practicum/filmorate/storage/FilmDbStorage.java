package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.storageInterface.DirectorStorage;
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
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;

    public Film create(Film film) {
        String sqlQuery = "insert into FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE,DURATION,MPA) " +
                "values (?, ?, ?, ?, ?)";
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
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genreStorage.addGenreToFilm(film.getId(), genre.getId());
            }
            film.setGenres(new HashSet<>(genreStorage.getGenreSetByFilm(film.getId())));//заполним из бд
        }
        //добавим режиссёров
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                directorStorage.addDirectorToFilm(film.getId(), director.getId());
            }
            film.setDirectors(new HashSet<>(directorStorage.getFilmDirectors(film.getId())));
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
        directorStorage.deleteDirectorsByFilm(film.getId());
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                directorStorage.addDirectorToFilm(film.getId(), director.getId());
            }
            film.setDirectors(new HashSet<>(directorStorage.getFilmDirectors(film.getId())));
        }
        return film;
    }

    @Override
    public boolean remove(long id) {
        var sql = "delete from FILMS where FILM_ID = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows != 0;
    }

    public List<Film> findAll() {
        String sql = "select * from FILMS ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    public Optional<Film> findFilmById(long id) {
        String sql = "select * from FILMS where FILM_ID = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (films.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(films.get(0));
        }
    }

    public Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("FILM_ID");
        String description = rs.getString("DESCRIPTION");
        String name = rs.getString("FILM_NAME");
        Date release = rs.getDate("RELEASE_DATE");
        LocalDate releaseDate;
        if (release == null) {
            releaseDate = null;
        } else {
            releaseDate = release.toLocalDate();
        }
        long duration = rs.getLong("DURATION");
        MPA mpa = mpaStorage.findMpaById(rs.getInt("MPA")).get();
        Set<Genre> genres = new HashSet<>(genreStorage.getGenreSetByFilm(id));
        Set<Director> directors = new HashSet<>(directorStorage.getFilmDirectors(id));
        return new Film(id, name, description, releaseDate, duration, mpa, genres, directors);
    }

    public List<Film> findPopular(int count) {
        String sqlQuery = "select F.FILM_ID as FILM_ID,  f.DESCRIPTION as DESCRIPTION" +
                ", f.DURATION as DURATION, f.FILM_NAME as FILM_NAME, f.MPA as MPA, f.RELEASE_DATE as RELEASE_DATE " +
                "from FILMS F  left join LIKES l on F.FILM_ID = l.FILM_ID  group by F.FILM_ID " +
                "order by  COUNT(distinct l.USER_ID) desc limit ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public List<Film> getMostPopularFilms(long count, long genreId, long year) {
        String sqlQuery;
        if ((year == 0) && (genreId == 0)) {
            return findPopular((int) (count));
        }
        if (year == 0) {
            sqlQuery = "select F.FILM_ID as FILM_ID,  f.DESCRIPTION as DESCRIPTION" +
                    ", f.DURATION as DURATION, f.FILM_NAME as FILM_NAME, f.MPA as MPA" +
                    ", f.RELEASE_DATE as RELEASE_DATE " +
                    "from FILMS F  " +
                    "left join LIKES l on F.FILM_ID = l.FILM_ID " +
                    "left join FILM_GENRES G on F.FILM_ID = G.FILM_ID " +
                    "where GENRE_ID = ?" +
                    "group by F.FILM_ID " +
                    "order by  COUNT(distinct l.USER_ID) desc limit ?";
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), genreId, count);
        }
        if (genreId == 0) {
            sqlQuery = "select F.FILM_ID as FILM_ID,  f.DESCRIPTION as DESCRIPTION" +
                    ", f.DURATION as DURATION, f.FILM_NAME as FILM_NAME, f.MPA as MPA" +
                    ", f.RELEASE_DATE as RELEASE_DATE " +
                    "from FILMS F  left join LIKES l on F.FILM_ID = l.FILM_ID  " +
                    "where EXTRACT(YEAR FROM (f.RELEASE_DATE)) = ?" +
                    "group by F.FILM_ID " +
                    "order by  COUNT(distinct l.USER_ID) desc limit ?";
            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), year, count);
        }
        sqlQuery = "select F.FILM_ID as FILM_ID,  f.DESCRIPTION as DESCRIPTION" +
                ", f.DURATION as DURATION, f.FILM_NAME as FILM_NAME, f.MPA as MPA, f.RELEASE_DATE as RELEASE_DATE " +
                "from (FILMS F  left join LIKES l on F.FILM_ID = l.FILM_ID) " +
                "left join FILM_GENRES G on F.FILM_ID = G.FILM_ID " +
                "where G.GENRE_ID = ? AND EXTRACT(YEAR FROM (RELEASE_DATE)) = ?" +
                "group by F.FILM_ID " +
                "order by  COUNT(distinct l.USER_ID) desc limit ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), genreId, year, count);
    }

    @Override
    public List<Film> findCommonFilms(long userId, long friendId) {
        String sqlQuery = "select F.FILM_ID as FILM_ID,  f.DESCRIPTION as DESCRIPTION" +
                ", f.DURATION as DURATION, f.FILM_NAME as FILM_NAME, f.MPA as MPA, f.RELEASE_DATE as RELEASE_DATE " +
                "from FILMS F  left join LIKES l on F.FILM_ID = l.FILM_ID  " +
                "left join LIKES fr on F.FILM_ID = fr.FILM_ID " +
                "where l.USER_ID = ? and fr.USER_ID = ?" +
                "group by F.FILM_ID " +
                "order by  COUNT(distinct l.USER_ID) desc";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), userId, friendId);
    }

    @Override
    public List<Film> findRecommendations(long userId) {
        String sqlQuery = "with result as(\n" +
                "select F.USER_ID, count(F.USER_ID) AS USER_COUNT,\n" +
                "max(count(F.USER_ID)) over (partition by L.USER_ID) as MAX_COUNT\n" +
                "from LIKES AS L\n" +
                "left join LIKES F on F.FILM_ID = L.FILM_ID\n" +
                "WHERE L.USER_ID = ? AND F.USER_ID <> ?\n" +
                "GROUP BY F.USER_ID)\n" +
                "select *\n" +
                "from FILMS\n" +
                "where FILM_ID in(\n" +
                "select FILM_ID\n" +
                "from LIKES\n" +
                "where USER_ID in (select USER_ID from result where MAX_COUNT = USER_COUNT)\n" +
                "and FILM_ID not in(select FILM_ID from Likes where USER_ID = ?))";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), userId, userId, userId);
    }

    @Override
    public List<Film> findDirectorsFilms(long directorId, String sortBy) {
        String sqlSort = "";
        if (sortBy.equals("year")) {
            sqlSort = "order by F.RELEASE_DATE ";
        } else if (sortBy.equals("likes")) {
            sqlSort = "order by COUNT(L.USER_ID) desc";
        }
        String sqlQuery = "select F.* FROM FILMS F " +
                "left join LIKES L on F.FILM_ID = L.FILM_ID " +
                "left join FILM_DIRECTORS FD on F.FILM_ID = FD.FILM_ID " +
                "where FD.DIRECTOR_ID in (" +
                "    select DIRECTOR_ID from DIRECTORS D " +
                "    where D.DIRECTOR_ID = ?) " +
                "group by F.FILM_ID " + sqlSort;
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), directorId);
    }

    @Override
    public List<Film> findFilms(String query, Set<String> searchKeys) {
        var builder = new StringBuilder();
        var args = new ArrayList<String>();
        builder.append(
                "select F.FILM_ID, f.DESCRIPTION, f.DURATION, f.FILM_NAME, f.MPA, f.RELEASE_DATE " +
                        "from FILMS F " +
                        "left join LIKES l on F.FILM_ID = l.FILM_ID "
        );
        for (var key : searchKeys) {
            switch (key) {
                case "director":
                    builder.append("left outer join FILM_DIRECTORS FD on FD.FILM_ID = F.FILM_ID ");
                    builder.append("left outer join DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID ");
                    break;
                case "title":
                    break;
                default:
                    throw new UnsupportedOperationException("Неподдерживаемый ключ поиска: '" + key + "'");
            }
        }
        boolean haveWhere = false;
        for (var key : searchKeys) {
            switch (key) {
                case "director":
                    if (haveWhere) {
                        builder.append("or");
                    } else {
                        builder.append("where");
                    }
                    builder.append(" lower(D.DIRECTOR_NAME) like ? ");
                    args.add("%" + query.toLowerCase() + "%");
                    haveWhere = true;
                    break;
                case "title":
                    if (haveWhere) {
                        builder.append("or");
                    } else {
                        builder.append("where");
                    }
                    builder.append(" lower(F.FILM_NAME) like ? ");
                    args.add("%" + query.toLowerCase() + "%");
                    haveWhere = true;
                    break;
                default:
                    throw new UnsupportedOperationException("Неподдерживаемый ключ поиска: '" + key + "'");
            }
        }
        builder.append(
                "group by F.FILM_ID " +
                        "order by COUNT(distinct l.USER_ID) desc"
        );
        return jdbcTemplate.query(builder.toString(), (rs, rowNum) -> makeFilm(rs), args.toArray());
    }
}