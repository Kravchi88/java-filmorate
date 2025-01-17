package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserEvent {
    Long timestamp;
    String userId;
    String eventType;
    String operation;
    long eventId;
    Long entityId;
}