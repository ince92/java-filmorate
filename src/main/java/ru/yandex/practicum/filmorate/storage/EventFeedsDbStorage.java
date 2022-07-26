package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.forEvent.EventTypes;
import ru.yandex.practicum.filmorate.model.forEvent.Operations;
import ru.yandex.practicum.filmorate.storage.storageInterface.EventFeedsStorage;

import java.sql.*;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventFeedsDbStorage<T> implements EventFeedsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(long userId, EventTypes eventType, Operations operation, long entityId) {
        String sqlQuery = "insert into EVENT_FEEDS (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID)" +
                " values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        long date = Instant.now().toEpochMilli();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"EVENT_ID"});
            stmt.setLong(1, date);
            stmt.setLong(2, userId);
            stmt.setString(3, eventType.toString());
            stmt.setString(4, operation.toString());
            stmt.setLong(5, entityId);
            return stmt;
        }, keyHolder);
    }

    @Override
    public List<Event> showUserHistory(long userId) {
        String sql = "select * from EVENT_FEEDS where USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    public Event makeEvent(ResultSet rs) throws SQLException {
        return new Event(
                rs.getLong("EVENT_ID"),
                rs.getLong("USER_ID"),
                rs.getLong("ENTITY_ID"),
                EventTypes.valueOf(rs.getString("EVENT_TYPE")),
                Operations.valueOf(rs.getString("OPERATION")),
                rs.getLong("TIMESTAMP")
        );
    }
}
