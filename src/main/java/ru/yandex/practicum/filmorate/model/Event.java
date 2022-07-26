package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.forEvent.EventTypes;
import ru.yandex.practicum.filmorate.model.forEvent.Operations;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private long eventId;
    private long userId;
    private long entityId;
    private EventTypes eventType;
    private Operations operation;
    private long timestamp;
}

