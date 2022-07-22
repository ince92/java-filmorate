package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.forEvent.EventTypes;
import ru.yandex.practicum.filmorate.model.forEvent.Operations;
import ru.yandex.practicum.filmorate.storage.storageInterface.EventFeedsStorage;
import ru.yandex.practicum.filmorate.storage.storageInterface.LikesStorage;

@Component
public class LikesDBStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    private final EventFeedsStorage eventFeedsStorage;

    public LikesDBStorage(JdbcTemplate jdbcTemplate, EventFeedsStorage eventFeedsStorage){
        this.jdbcTemplate = jdbcTemplate;
        this.eventFeedsStorage = eventFeedsStorage;
    }

    public void addLike(long id, long userId) {
        String sqlQuery = "insert into LIKES (FILM_ID, USER_ID) values (?, ?)";

        jdbcTemplate.update(sqlQuery,id,userId);
        eventFeedsStorage.addEvent(userId, EventTypes.LIKE, Operations.ADD, id);

    }

    public void deleteLike(long id, long userId) {
        String sqlQuery = "delete from LIKES where FILM_ID= ? AND USER_ID= ?";

        jdbcTemplate.update(sqlQuery,id,userId);
        eventFeedsStorage.addEvent(userId, EventTypes.LIKE, Operations.REMOVE, id);
    }
}
