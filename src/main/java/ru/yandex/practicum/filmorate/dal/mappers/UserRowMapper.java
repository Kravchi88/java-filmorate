package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a row from the 'users' table in the database to a {@link User} object.
 */
@Component
public class UserRowMapper implements RowMapper<User> {

    /**
     * Maps a single row of the ResultSet to a User object.
     *
     * @param rs     the ResultSet returned by the query.
     * @param rowNum the row number being processed.
     * @return a constructed User object with data from the current row.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("user_email"));
        user.setLogin(rs.getString("user_login"));
        user.setName(rs.getString("user_name"));
        user.setBirthday(rs.getDate("user_birthday").toLocalDate());
        return user;
    }
}