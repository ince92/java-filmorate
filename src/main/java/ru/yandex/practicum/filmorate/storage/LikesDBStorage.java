package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public class LikesDBStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    public LikesDBStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage){
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
    }

    public void addLike(long id, long userId) {
        String sqlQuery = "insert into LIKES (FILM_ID, USER_ID) values (?, ?)";

        jdbcTemplate.update(sqlQuery,id,userId);


    }

    public void deleteLike(long id, long userId) {
        String sqlQuery = "delete from LIKES where FILM_ID= ? AND USER_ID= ?";

        jdbcTemplate.update(sqlQuery,id,userId);

    }

    public List<Film> findPopular(int count) {
        String sqlQuery = "select F.FILM_ID as FILM_ID,  f.DESCRIPTION as DESCRIPTION" +
                ", f.DURATION as DURATION, f.FILM_NAME as FILM_NAME, f.MPA as MPA, f.RELEASE_DATE as RELEASE_DATE "+
                "from FILMS F  left join LIKES l on F.FILM_ID = l.FILM_ID  group by F.FILM_ID " +
                "order by  COUNT(distinct l.USER_ID) desc limit ?";

        return jdbcTemplate.query(sqlQuery,(rs, rowNum) -> filmDbStorage.makeFilm(rs),count);


    }
}
