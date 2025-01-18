package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a row from the 'reviews' table in the database to a {@link ReviewDto} object.
 */
@Component
public class ReviewRowMapper implements RowMapper<ReviewDto> {

    /**
     * Maps a single row of the ResultSet to a ReviewDto object.
     *
     * @param rs     the ResultSet returned by the query.
     * @param rowNum the row number being processed.
     * @return a constructed ReviewDto object with data from the current row.
     * @throws SQLException if there is an error accessing the ResultSet.
     */
    @Override
    public ReviewDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReviewDto.builder()
                .reviewId(rs.getLong("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getInt("status"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
    }
}
