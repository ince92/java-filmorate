package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.storageInterface.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<User> findAll() {
        String sql = "select * from USERS ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    public User create(User user) {
        String sqlQuery = "insert into USERS (USER_NAME, EMAIL, BIRTHDAY,LOGIN) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setDate(3, Date.valueOf(user.getBirthday()));
            stmt.setString(4, user.getLogin());
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }


    public User update(User user) {
        String sqlQuery = "update USERS set " +
                "USER_NAME = ?, EMAIL = ?, BIRTHDAY = ?, LOGIN = ? " +
                "where USER_ID = ?";
        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getEmail()
                , Date.valueOf(user.getBirthday())
                , user.getLogin()
                , user.getId());

        return user;
    }

    @Override
    public boolean remove(long id) {
        var sql = "delete from USERS where USER_ID = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows != 0;
    }

    public Optional<User> findUserById(long id) {
        String sql = "select * from USERS where USER_ID = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        if (users.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    public static User makeUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("USER_ID");
        String name = rs.getString("USER_NAME");
        String email = rs.getString("EMAIL");
        Date birthdayDate = rs.getDate("BIRTHDAY");
        LocalDate birthday;
        if (birthdayDate == null) {
            birthday = null;
        } else {
            birthday = birthdayDate.toLocalDate();
        }
        String login = rs.getString("LOGIN");
        return new User(id, email, login, name, birthday);
    }
}
