package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.storageInterface.FriendshipStorage;

import java.util.List;
@Component
public class FriendshipDBStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDBStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;

    }


    public boolean addFriend(long userId, long friendId) {

        String sqlQuery = "merge into FRIENDSHIPS (USER_ID, FRIEND_ID) values (?, ?)";

        jdbcTemplate.update(sqlQuery,userId,friendId);

        return true;
    }

    public boolean deleteFriend(long userId, long friendId) {

        String sqlQuery = "delete from FRIENDSHIPS where USER_ID= ? and FRIEND_ID=?";

        jdbcTemplate.update(sqlQuery,userId,friendId);

        return true;
    }

    public List<User> findFriends(long userId) {
        String sqlQuery = "select U.USER_ID ,  U.USER_NAME" +
                ",  U.EMAIL,  U.BIRTHDAY,  U.LOGIN  "+
                "from FRIENDSHIPS as F left join USERS U on F.FRIEND_ID = U.USER_ID where F.USER_ID = ?";

       return jdbcTemplate.query (sqlQuery,(rs, rowNum) -> UserDbStorage.makeUser(rs),userId);



    }

    public List<User> findCommonFriends(long userId, long otherId) {

        String sqlQuery = "select U.USER_ID ,  U.USER_NAME" +
                ",  U.EMAIL,  U.BIRTHDAY,  U.LOGIN  "+
                "from FRIENDSHIPS as F inner join FRIENDSHIPS F2 on F.FRIEND_ID = F2.FRIEND_ID left join USERS U on "+
                "F.FRIEND_ID = U.USER_ID where F.USER_ID = ? and F2.USER_ID = ?";

        return jdbcTemplate.query (sqlQuery,(rs, rowNum) -> UserDbStorage.makeUser(rs),userId,otherId);


    }
}
