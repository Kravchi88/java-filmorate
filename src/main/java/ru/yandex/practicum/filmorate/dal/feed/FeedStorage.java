package ru.yandex.practicum.filmorate.dal.feed;

import ru.yandex.practicum.filmorate.model.UserEvent;

public interface FeedStorage {

    void addEvent(UserEvent userEvent);
}
