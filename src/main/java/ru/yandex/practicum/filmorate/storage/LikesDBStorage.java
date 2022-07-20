package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.forEvent.EventTypes;
import ru.yandex.practicum.filmorate.model.forEvent.Operations;
import ru.yandex.practicum.filmorate.storage.storageInterface.LikesStorage;

@Component
public class LikesDBStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    private final EventFeedsDbStorage eventFeedsDbStorage;

    public LikesDBStorage(JdbcTemplate jdbcTemplate, EventFeedsDbStorage eventFeedsDbStorage){
        this.jdbcTemplate = jdbcTemplate;
        this.eventFeedsDbStorage = eventFeedsDbStorage;
    }

    public void addLike(long id, long userId) {
        String sqlQuery = "insert into LIKES (FILM_ID, USER_ID) values (?, ?)";

        jdbcTemplate.update(sqlQuery,id,userId);
        eventFeedsDbStorage.addEvent(userId, EventTypes.LIKE, Operations.ADD, id);

    }

    public void deleteLike(long id, long userId) {
        String sqlQuery = "delete from LIKES where FILM_ID= ? AND USER_ID= ?";

        jdbcTemplate.update(sqlQuery,id,userId);
        eventFeedsDbStorage.addEvent(userId, EventTypes.LIKE, Operations.REMOVE, id);
    }
}
