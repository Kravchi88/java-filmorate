package ru.yandex.practicum.filmorate.dal.feed;

import ru.yandex.practicum.filmorate.model.UserEvent;

public interface FeedStorage {
    /**
     * Adds a new user event.
     *
     * @param userEvent the UserEvent object representing the new event
     */
    void addEvent(UserEvent userEvent);
}
