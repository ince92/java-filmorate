package ru.yandex.practicum.filmorate.storage.storageInterface;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.forEvent.EventTypes;
import ru.yandex.practicum.filmorate.model.forEvent.Operations;

import java.util.List;

public interface EventFeedsStorage {

    List<Event> showUserHistory(long userId);
    void addEvent(long userId, EventTypes eventType, Operations operation, long entityId);
}
