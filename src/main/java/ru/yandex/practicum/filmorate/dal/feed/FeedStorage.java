package ru.yandex.practicum.filmorate.dal.feed;

import ru.yandex.practicum.filmorate.model.UserEvent;

public interface FeedStorage {
    /**
     * Добавляет новое событие пользователя.
     *
     * @param userEvent объект UserEvent, представляющий новое событие
     */
    void addEvent(UserEvent userEvent);
}
