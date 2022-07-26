package ru.yandex.practicum.filmorate.storage.storageInterface;


public interface
LikesStorage {

    void addLike(long id, long userId);
    void deleteLike(long id, long userId);

}
