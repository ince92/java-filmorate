package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventFeedsDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.LikesDBStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

class EventFeedsDbStorageTest {

    private final EventFeedsDbStorage eventFeedsDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikesDBStorage likesDBStorage;

    @Test
    void showUserHistory() {
        filmDbStorage.create(new Film(1L, "Film", "Action", LocalDate.of(1990, 1, 1),
                7200L, new MPA(1),new HashSet<>(), null));
        userDbStorage.create(new User(1, "emai@l", "login", "name", LocalDate.of(1992, 9,
                4)));
        likesDBStorage.addLike(1,1);
        assertEquals(1, eventFeedsDbStorage.showUserHistory(1).size());
    }
}