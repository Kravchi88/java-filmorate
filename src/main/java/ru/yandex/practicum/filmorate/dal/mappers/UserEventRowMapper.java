package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

@Component
public class UserEventRowMapper implements RowMapper<UserEvent> {

    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEvent userEvent = new UserEvent();
        userEvent.setEventId(rs.getLong("event_id"));
        userEvent.setUserId(rs.getString("user_id"));
        userEvent.setEventType(rs.getString("event_type"));
        userEvent.setOperation(rs.getString("operation"));
        userEvent.setEntityId(rs.getLong("entity_id"));
        userEvent.setTimestamp(rs.getDate("timestamp").toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        return userEvent;
    }
}