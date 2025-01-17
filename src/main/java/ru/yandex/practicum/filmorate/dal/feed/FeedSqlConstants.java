package ru.yandex.practicum.filmorate.dal.feed;

public interface FeedSqlConstants {

   String INSERT_USER_EVENTS = "INSERT INTO user_events (user_id, event_type, operation, entity_id ,timestamp) VALUES (?, ?, ?, ?, ?)";
}
