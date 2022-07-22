package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.storageInterface.EventFeedsStorage;

import java.util.List;

@Service
@Slf4j
public class EventService {

    EventFeedsStorage eventFeedsStorage;


    public EventService(EventFeedsStorage eventFeedsStorage) {
        this.eventFeedsStorage = eventFeedsStorage;
    }

    public List<Event> showUserHistory (long userId) {
        return eventFeedsStorage.showUserHistory(userId);
    }
}
