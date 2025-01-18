package ru.yandex.practicum.filmorate.dal.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.PreparedStatement;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.dal.feed.FeedSqlConstants.INSERT_USER_EVENTS;

@Component
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEvent(UserEvent userEvent) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_USER_EVENTS, new String[]{"event_id"});
            ps.setLong(1, userEvent.getUserId());
            ps.setString(2, userEvent.getEventType());
            ps.setString(3, userEvent.getOperation());
            ps.setLong(4, userEvent.getEntityId());
            ps.setDate(5, new java.sql.Date(userEvent.getTimestamp()));
            return ps;

        }, keyHolder);

        userEvent.setEventId(Objects.requireNonNull(keyHolder.getKey().longValue()));

    }
}
