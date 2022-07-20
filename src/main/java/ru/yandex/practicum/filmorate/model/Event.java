package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.forEvent.EventTypes;
import ru.yandex.practicum.filmorate.model.forEvent.Operations;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Timestamp timestamp;
    private Long userId;
    private EventTypes eventType;
    private Operations operation;
    private Long evenId;
    private Long entityId;
}

