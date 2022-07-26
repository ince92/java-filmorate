package ru.yandex.practicum.filmorate.storage.storageInterface;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

     boolean addFriend(long userId, long friendId);
     boolean deleteFriend(long userId, long friendId);
     List<User> findFriends(long userId);
     List<User> findCommonFriends(long userId, long otherId);
}
